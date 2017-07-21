/*
 * jETeL/CloverETL - Java based ETL application framework.
 * Copyright (c) Javlin, a.s. (info@cloveretl.com)
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jetel.data.xsd;

import javax.xml.bind.DatatypeConverter;
import org.apache.log4j.Logger;
import org.jetel.exception.DataConversionException;
import org.jetel.metadata.DataFieldMetadata;

/**
 *
 * @author Pavel Pospichal
 */
public class CloverNumericConvertor implements IGenericConvertor {

    private static Logger logger = Logger.getLogger(CloverNumericConvertor.class);
    
    static {
    	ConvertorRegistry.registerConvertor(new CloverNumericConvertor());
    }
    
    public static Double parseXsdDoubleToDouble(String value) throws DataConversionException {
        Double result = null;
        
        try {
            result = DatatypeConverter.parseDouble(value);
        } catch(Exception e) {
            logger.fatal("Unable to parse xsd:double to "+Double.class.getName()+".",e);
            throw new DataConversionException("Unable to parse xsd:double to "+Double.class.getName()+".", e);
        }
        
        return result;
    }
    
    public static String printDoubleToXsdDouble(Double value) throws DataConversionException {
        String result = null;
        
        try {
            result = DatatypeConverter.printDouble(value);
        } catch(Exception e) {
            logger.fatal("Unable to print "+Double.class.getName()+" to xsd:double.",e);
            throw new DataConversionException("Unable to print "+Double.class.getName()+" to xsd:double.", e);
        }
        
        return result;
    }

    @Override
	public Object parse(String input) throws DataConversionException {
        return parseXsdDoubleToDouble(input);
    }

    @Override
	public String print(Object obj) throws DataConversionException {
        if (!(obj instanceof Double)) {
            throw new DataConversionException("Unsupported type by convertion: " + obj.getClass().getName());
        }

        return printDoubleToXsdDouble((Double) obj);
    }

	@Override
	public boolean supportsCloverType(String cloverDataTypeCriteria) {
		return DataFieldMetadata.NUMERIC_TYPE.equals(cloverDataTypeCriteria);
	}
	
	@Override
	public boolean supportsExternalSystemType(String externalTypeCriteria) {
		return true;
	}
}
