/*
*    jETeL/Clover - Java based ETL application framework.
*    Copyright (C) 2002-04  David Pavlis <david_pavlis@hotmail.com>
*    
*    This library is free software; you can redistribute it and/or
*    modify it under the terms of the GNU Lesser General Public
*    License as published by the Free Software Foundation; either
*    version 2.1 of the License, or (at your option) any later version.
*    
*    This library is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU    
*    Lesser General Public License for more details.
*    
*    You should have received a copy of the GNU Lesser General Public
*    License along with this library; if not, write to the Free Software
*    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*
*/
/*
 *  Created on Mar 19, 2003
 *
 *  To change this generated comment go to
 *  Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.jetel.component;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetel.data.DataRecord;
import org.jetel.data.parser.FixLenByteDataParser;
import org.jetel.data.parser.FixLenCharDataParser;
import org.jetel.data.parser.FixLenDataParser;
import org.jetel.exception.BadDataFormatException;
import org.jetel.exception.ComponentNotReadyException;
import org.jetel.exception.ConfigurationProblem;
import org.jetel.exception.ConfigurationStatus;
import org.jetel.exception.IParserExceptionHandler;
import org.jetel.exception.ParserExceptionHandlerFactory;
import org.jetel.exception.PolicyType;
import org.jetel.exception.XMLConfigurationException;
import org.jetel.graph.Node;
import org.jetel.graph.Result;
import org.jetel.graph.TransformationGraph;
import org.jetel.util.MultiFileReader;
import org.jetel.util.SynchronizeUtils;
import org.jetel.util.property.ComponentXMLAttributes;
import org.jetel.util.string.StringUtils;
import org.w3c.dom.Element;

/**
 *  <h3>Fixed Length Data NIO Reader Component</h3>
 *
 * <!-- Parses specified input data file and broadcasts the records to all connected out ports -->
 *
 * <table border="1">
 *  <th>Component:</th>
 * <tr><td><h4><i>Name:</i></h4></td>
 * <td>FixLenDataReader</td></tr>
 * <tr><td><h4><i>Category:</i></h4></td>
 * <td></td></tr>
 * <tr><td><h4><i>Description:</i></h4></td>
 * <td>Parses specified fixed-length record, input data file and broadcasts the records to all connected out ports.
 * The length of record is fixed either in bytes or in chars. Byte mode is somewhat stricter than char mode.
 * It doesn't support record delimiters, incomplete records and skipping of leading/trailing blanks in fields.
 * </td></tr>
 * <tr><td><h4><i>Inputs:</i></h4></td>
 * <td></td></tr>
 * <tr><td><h4><i>Outputs:</i></h4></td>
 * <td>At least one output port defined/connected.</td></tr>
 * <tr><td><h4><i>Comment:</i></h4></td>
 * <td></td></tr>
 * </table>
 *  <br>
 *  <table border="1">
 *  <th>XML attributes:</th>
 *  <tr><td><b>type</b></td><td>"FIXLEN_DATA_READER"</td></tr>
 *  <tr><td><b>id</b></td><td>component identification</td>
 *  <tr><td><b>byteMode</b></td><td>Specifies parsing mode. true for byte mode, false for char mode.
 *  Default is false.</td>
 *  <tr><td><b>fileURL</b></td><td>path to the input file</td>
 *  <tr><td><b>skipLeadingBlanks</b></td><td>Values true/false. Sets on/off skipping of leading blanks in fields.
 *  Default is true. It doesn't have any effect in byte mode.</td>
 *  <tr><td><b>skipTrailingBlanks</b></td><td>Values true/false. Sets on/off skipping of trailing blanks in fields.
 *  Default is true. It doesn't have any effect in byte mode.</td>
 *  <tr><td><b>trim</b><br><i>optional</i></td><td>specifies whether to trim strings before setting them to data fields.
 *  When not set, strings are trimmed depending on "trim" attribute of metadata.</td>
 *  <tr><td><b>enableIncomplete</b></td><td>Values true/false. Sets on/off support for incomplete records. 
 *  Default is true. It doesn't have any effect in byte mode.</td>
 *  <tr><td><b>skipEmpty</b></td><td>Values true/false. Specifies whether empty records are to be ignored. 
 *  Default is true. It doesn't have any effect in byte mode.</td>
 *  <tr><td><b>charset</b></td><td>character encoding of the input file (if not specified, then ISO-8859-1 is used)</td>
 *  <tr><td><b>dataPolicy</b></td><td>specifies how to handle misformatted or incorrect data.  'Strict' (default value) aborts processing, 'Controlled' logs the entire record while processing continues, and 'Lenient' attempts to set incorrect data to default values while processing continues.</td>
 *  <tr><td><b>skipFirstLine</b></td><td>specifies whether first record/line should be skipped. Default value is FALSE. If record delimiter is specified than skip one record else first line of flat file.</td>
 *  <tr><td><b>skipRows</b><br><i>optional</i></td><td>specifies how many records/rows should be skipped from the source file. Good for handling files where first rows is a header not a real data. Dafault is 0.</td>
 *  <tr><td><b>numRecords</b></td><td>max number of parsed records</td>
 *  </tr>
 *  </table>
 *
 *  <h4>Example:</h4>
 *  <pre>&lt;Node type="FIXED_DATA_READER" id="InputFile" fileURL="/tmp/mydata.dat" /&gt;</pre>
 *
 * @author Jan Hadrava (jan.hadrava@javlinconsulting.cz), Javlin Consulting (www.javlinconsulting.cz)
 * @since 09/15/06  
 * @see         org.jetel.data.parser.FixLenDataParser
 */

public class FixLenDataReader extends Node {

	private static final String XML_BYTEMODE_ATTRIBUTE = "byteMode";
	private static final String XML_SKIPLEADINGBLANKS_ATTRIBUTE = "skipLeadingBlanks";
	private static final String XML_SKIPTRAILINGBLANKS_ATTRIBUTE = "skipTrailingBlanks";
	private static final String XML_ENABLEINCOMPLETE_ATTRIBUTE = "enableIncomplete";
	private static final String XML_SKIPEMPTY_ATTRIBUTE = "skipEmpty";
	private static final String XML_FILEURL_ATTRIBUTE = "fileURL";
	private static final String XML_CHARSET_ATTRIBUTE = "charset";
	private final static String XML_DATAPOLICY_ATTRIBUTE = "dataPolicy";
    private static final String XML_SKIPFIRSTLINE_ATTRIBUTE = "skipFirstLine";
	private static final String XML_SKIP_ROWS_ATTRIBUTE = "skipRows";
    private static final String XML_NUMRECORDS_ATTRIBUTE = "numRecords";
	private static final String XML_TRIM_ATTRIBUTE = "trim";
	private static final String XML_INCREMENTAL_FILE_ATTRIBUTE = "incrementalFile";
	private static final String XML_INCREMENTAL_KEY_ATTRIBUTE = "incrementalKey";

	
	static Log logger = LogFactory.getLog(FixLenDataReader.class);
	
	/**  Description of the Field */
	public final static String COMPONENT_TYPE = "FIXLEN_DATA_READER";

	private final static int INPUT_PORT = 0;
	private final static int OUTPUT_PORT = 0;
	private String fileURL;

	private FixLenDataParser parser;
    private MultiFileReader reader;
    private PolicyType policyType;
    private boolean skipFirstLine = false;
	private int skipRows = 0; // do not skip rows by default
    private int numRecords = -1;
    private String incrementalFile;
    private String incrementalKey;

	private boolean byteMode;
	private String charset;

	/**
	 *Constructor for the FixLenDataReaderNIO object
	 *
	 * @param  id       Description of the Parameter
	 * @param  fileURL  Description of the Parameter
	 */
	public FixLenDataReader(String id, String fileURL,
			boolean byteMode) {
		super(id);
		this.byteMode = byteMode; 
		this.fileURL = fileURL;
		parser = byteMode ?
				new FixLenByteDataParser() :
				new FixLenCharDataParser();
	}


	/**
	 *Constructor for the FixLenDataReaderNIO object
	 *
	 * @param  id       Description of the Parameter
	 * @param  fileURL  Description of the Parameter
	 * @param  charset  Description of the Parameter
	 */
	public FixLenDataReader(String id, String fileURL, String charset,
			boolean byteMode) {
		super(id);
		this.byteMode = byteMode; 
		this.fileURL = fileURL;
		this.charset = charset;
		parser = byteMode ?
				new FixLenByteDataParser(charset) :
				new FixLenCharDataParser(charset);
	}

	@Override
	public Result execute() throws Exception {
		// we need to create data record - take the metadata from first output port
		DataRecord record = new DataRecord(getOutputPort(OUTPUT_PORT).getMetadata());
		record.init();

		try {
			while (record != null && runIt) {
			    try {
					//broadcast the record to all connected Edges
			        if((record = reader.getNext(record)) != null) {
						writeRecordBroadcast(record);
			        }
			    } catch(BadDataFormatException bdfe) {
			        if(policyType == PolicyType.STRICT) {
			            throw bdfe;
			        } else {
			            logger.warn(bdfe.getMessage());
			        }
			    }
			    SynchronizeUtils.cloverYield();
			}
		} catch (Exception e) {
			throw e;
		}finally{
			reader.close();
			broadcastEOF();
		}
        return runIt ? Result.FINISHED_OK : Result.ABORTED;
	}


	/**
	 *  Description of the Method
	 *
	 * @exception  ComponentNotReadyException  Description of the Exception
	 * @since                                  April 4, 2002
	 */
	public void init() throws ComponentNotReadyException {
        if(isInitialized()) return;
		super.init();
        
		prepareMultiFileReader();
		reader.init(getOutputPort(OUTPUT_PORT).getMetadata());
	}

	private void prepareMultiFileReader() throws ComponentNotReadyException {
        // initialize multifile reader based on prepared parser
		TransformationGraph graph = getGraph();
        reader = new MultiFileReader(parser, graph != null ? graph.getProjectURL() : null, fileURL);
        reader.setLogger(logger);
        reader.setFileSkip(skipFirstLine ? 1 : 0);
        reader.setSkip(skipRows);
        reader.setNumRecords(numRecords);
        reader.setIncrementalFile(incrementalFile);
        reader.setIncrementalKey(incrementalKey);
        reader.setInputPort(getInputPort(INPUT_PORT)); //for port protocol: ReadableChannelIterator reads data
        reader.setCharset(charset);
        reader.setDictionary(graph.getDictionary());
	}

	@Override
	public synchronized void reset() throws ComponentNotReadyException {
		super.reset();
		parser.reset();
		reader.reset();
	}

    @Override
    public synchronized void free() {
    	super.free();
    	storeValues();
    	reader.close();
    }

    /**
     * Stores all values as incremental reading.
     */
    private void storeValues() {
    	if (getPhase() != null && getPhase().getResult() == Result.FINISHED_OK) {
    		try {
				reader.storeIncrementalReading();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
    	}
    }
    
    /**
	 *  Description of the Method
	 *
	 * @return    Description of the Returned Value
	 * @since     May 21, 2002
	 */
	public void toXML(Element xmlElement) {
		super.toXML(xmlElement);
		xmlElement.setAttribute(XML_FILEURL_ATTRIBUTE,this.fileURL);
		
		if (this.isByteMode()) {
			xmlElement.setAttribute(XML_BYTEMODE_ATTRIBUTE,
					String.valueOf(this.isByteMode()));
		}
		
		if (this.parser.getCharsetName() != null) {
			xmlElement.setAttribute(XML_CHARSET_ATTRIBUTE, this.parser.getCharsetName());
		}
		
		xmlElement.setAttribute(XML_SKIPLEADINGBLANKS_ATTRIBUTE,
				String.valueOf(this.parser.isSkipLeadingBlanks()));
		
		xmlElement.setAttribute(XML_SKIPTRAILINGBLANKS_ATTRIBUTE,
				String.valueOf(this.parser.isSkipTrailingBlanks()));
		
		xmlElement.setAttribute(XML_ENABLEINCOMPLETE_ATTRIBUTE,
				String.valueOf(this.parser.isEnableIncomplete()));
		
		xmlElement.setAttribute(XML_SKIPEMPTY_ATTRIBUTE,
				String.valueOf(this.parser.isSkipEmpty()));
		
		if (this.skipRows>0){
		    xmlElement.setAttribute(XML_SKIP_ROWS_ATTRIBUTE, String.valueOf(skipRows));
		}
		xmlElement.setAttribute(XML_DATAPOLICY_ATTRIBUTE, policyType.toString());
		if (parser instanceof FixLenCharDataParser
				&& ((FixLenCharDataParser) parser).getTrim() != null) {
			xmlElement.setAttribute(XML_TRIM_ATTRIBUTE, String
					.valueOf(((FixLenCharDataParser) parser).getTrim()));
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  nodeXML  Description of Parameter
	 * @return          Description of the Returned Value
	 * @since           May 21, 2002
	 */
    public static Node fromXML(TransformationGraph graph, Element xmlElement) throws XMLConfigurationException {
		FixLenDataReader aFixLenDataReaderNIO = null;
		ComponentXMLAttributes xattribs = new ComponentXMLAttributes(xmlElement, graph);
		
		try {
			String charset = null;
			if (xattribs.exists(XML_CHARSET_ATTRIBUTE)) {
				charset = xattribs.getString(XML_CHARSET_ATTRIBUTE);
			}
			boolean byteMode = false;
			if (xattribs.exists(XML_BYTEMODE_ATTRIBUTE)){
				byteMode = xattribs.getBoolean(XML_BYTEMODE_ATTRIBUTE);
			}
			aFixLenDataReaderNIO = new FixLenDataReader(xattribs.getString(XML_ID_ATTRIBUTE),
						xattribs.getString(XML_FILEURL_ATTRIBUTE, ""),
						charset, byteMode);
			if (xattribs.exists(XML_ENABLEINCOMPLETE_ATTRIBUTE)){
				aFixLenDataReaderNIO.parser.setEnableIncomplete(xattribs.getBoolean(XML_ENABLEINCOMPLETE_ATTRIBUTE));
			}
			if (xattribs.exists(XML_SKIPEMPTY_ATTRIBUTE)){
				aFixLenDataReaderNIO.parser.setSkipEmpty(xattribs.getBoolean(XML_SKIPEMPTY_ATTRIBUTE));
			}
			if (xattribs.exists(XML_SKIPLEADINGBLANKS_ATTRIBUTE)){
				aFixLenDataReaderNIO.parser.setSkipLeadingBlanks(xattribs.getBoolean(XML_SKIPLEADINGBLANKS_ATTRIBUTE));
			}
			if (xattribs.exists(XML_SKIPTRAILINGBLANKS_ATTRIBUTE)){
				aFixLenDataReaderNIO.parser.setSkipTrailingBlanks(xattribs.getBoolean(XML_SKIPTRAILINGBLANKS_ATTRIBUTE));
			}
            if (xattribs.exists(XML_SKIPFIRSTLINE_ATTRIBUTE)){
                aFixLenDataReaderNIO.setSkipFirstLine(xattribs.getBoolean(XML_SKIPFIRSTLINE_ATTRIBUTE));
            }
			if (xattribs.exists(XML_SKIP_ROWS_ATTRIBUTE)){
				aFixLenDataReaderNIO.setSkipRows(xattribs.getInteger(XML_SKIP_ROWS_ATTRIBUTE));
			}
            if (xattribs.exists(XML_NUMRECORDS_ATTRIBUTE)){
                aFixLenDataReaderNIO.setNumRecords(xattribs.getInteger(XML_NUMRECORDS_ATTRIBUTE));
            }
			aFixLenDataReaderNIO.setPolicyType(xattribs.getString(XML_DATAPOLICY_ATTRIBUTE, null));			
			if (xattribs.exists(XML_TRIM_ATTRIBUTE)){
				aFixLenDataReaderNIO.setTrim(xattribs.getBoolean(XML_TRIM_ATTRIBUTE));
			}
			if (xattribs.exists(XML_INCREMENTAL_FILE_ATTRIBUTE)){
				aFixLenDataReaderNIO.setIncrementalFile(xattribs.getString(XML_INCREMENTAL_FILE_ATTRIBUTE));
			}
			if (xattribs.exists(XML_INCREMENTAL_KEY_ATTRIBUTE)){
				aFixLenDataReaderNIO.setIncrementalKey(xattribs.getString(XML_INCREMENTAL_KEY_ATTRIBUTE));
			}
		} catch (Exception ex) {
	           throw new XMLConfigurationException(COMPONENT_TYPE + ":" + xattribs.getString(XML_ID_ATTRIBUTE," unknown ID ") + ":" + ex.getMessage(),ex);
		}

		return aFixLenDataReaderNIO;
	}


	/**
	 * Adds BadDataFormatExceptionHandler to behave according to DataPolicy.
	 *
	 * @param  handler
	 */
	public void setExceptionHandler(IParserExceptionHandler handler) {
		parser.setExceptionHandler(handler);
	}


	/**  Description of the Method */
    @Override
    public ConfigurationStatus checkConfig(ConfigurationStatus status) {
        super.checkConfig(status);
        
        if(!checkInputPorts(status, 0, 1)
        		|| !checkOutputPorts(status, 1, Integer.MAX_VALUE)) {
        	return status;
        }
        
        checkMetadata(status, getOutMetadata());

        try {
    		prepareMultiFileReader();
    		reader.checkConfig(getOutputPort(OUTPUT_PORT).getMetadata());
        } catch (ComponentNotReadyException e) {
            ConfigurationProblem problem = new ConfigurationProblem(e.getMessage(), ConfigurationStatus.Severity.WARNING, this, ConfigurationStatus.Priority.NORMAL);
            if(!StringUtils.isEmpty(e.getAttributeName())) {
                problem.setAttributeName(e.getAttributeName());
            }
            status.add(problem);
        } finally {
        	free();
        }
        
        return status;
    }
	
	public String getType(){
		return COMPONENT_TYPE;
	}
    /**
     * @return Returns the skipRows.
     */
    public int getSkipRows() {
        return skipRows;
    }
    /**
     * @param skipRows The skipRows to set.
     */
    public void setSkipRows(int skipRows) {
        this.skipRows = skipRows;
    }
    
    public void setPolicyType(String strPolicyType) {
        setPolicyType(PolicyType.valueOfIgnoreCase(strPolicyType));
    }
    
	public void setPolicyType(PolicyType policyType) {
        this.policyType = policyType;
        parser.setExceptionHandler(ParserExceptionHandlerFactory.getHandler(policyType));
	}

	public PolicyType getPolicyType() {
		return this.parser.getPolicyType();
	}
	
    private boolean isByteMode() {
    	return byteMode;
    }
    
    public void setNumRecords(int numRecords) {
        this.numRecords = Math.max(numRecords, 0);
    }

    public void setSkipFirstLine(boolean skip) {
        this.skipFirstLine = skip;
    }

    public void setTrim(Boolean trim){
    	if (parser instanceof FixLenCharDataParser){
    		((FixLenCharDataParser)parser).setTrim(trim);
    	}
    }
    
    public void setIncrementalFile(String incrementalFile) {
    	this.incrementalFile = incrementalFile;
    }

    public void setIncrementalKey(String incrementalKey) {
    	this.incrementalKey = incrementalKey;
    }
}

