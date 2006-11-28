/*
*    jETeL/Clover - Java based ETL application framework.
*    Copyright (C) 2006 Javlin Consulting <info@javlinconsulting>
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
package org.jetel.component;

import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;

import org.jetel.data.DataRecord;
import org.jetel.exception.ComponentNotReadyException;
import org.jetel.metadata.DataRecordMetadata;

/**
 * Interface to be implemented by message processors for component JmsReader.
 * @author Jan Hadrava (jan.hadrava@javlinconsulting.cz), Javlin Consulting (www.javlinconsulting.cz)
 * @since 11/28/06  
 * @see org.jetel.component.JmsReader
 *
 */
public interface JmsMsg2DataRecord {
	/**
	 * Initialize the processor.
	 * @param metadata Metadata for the records which will be processed by other methods.
	 * @param props Contains remaining init parameters.
	 * @throws ComponentNotReadyException
	 */
	public void init(DataRecordMetadata metadata, Properties props) throws ComponentNotReadyException;
	
	/**
	 * May be used to end processing of input JMS messages
	 * @return
	 */
	public boolean endOfInput();
	
	/**
	 * Transform JMS message to data record.
	 * @param msg The message to be transformed
	 * @return Data record
	 * @throws JMSException
	 */
	public DataRecord extractRecord(Message msg) throws JMSException;
	
	/**
	 * Releases resources. 
	 */
	public void finished();
	
	/**
	 * Nomen omen.
	 * @return
	 */
	public String getErrorMsg();
}
