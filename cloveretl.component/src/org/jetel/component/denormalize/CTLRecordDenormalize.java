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
package org.jetel.component.denormalize;

import java.util.Properties;

import org.jetel.component.TransformUtils;
import org.jetel.ctl.CTLAbstractTransform;
import org.jetel.ctl.CTLEntryPoint;
import org.jetel.ctl.TransformLangExecutorRuntimeException;
import org.jetel.data.DataRecord;
import org.jetel.exception.ComponentNotReadyException;
import org.jetel.exception.TransformException;
import org.jetel.metadata.DataRecordMetadata;
import org.jetel.util.ExceptionUtils;

/**
 * Base class of all Java transforms generated by CTL-to-Java compiler from CTL transforms in the Denormalizer
 * component.
 *
 * @author Michal Tomcanyi, Javlin a.s. &lt;michal.tomcanyi@javlin.cz&gt;
 * @author Martin Janik, Javlin a.s. &lt;martin.janik@javlin.eu&gt;
 *
 * @version 17th June 2010
 * @created 2nd April 2009
 *
 * @see RecordDenormalize
 */
public abstract class CTLRecordDenormalize extends CTLAbstractTransform implements RecordDenormalize {

	public static final String APPEND_FUNCTION_NAME = "append";
	public static final String APPEND_ON_ERROR_FUNCTION_NAME = "appendOnError";
	public static final String TRANSFORM_FUNCTION_NAME = "transform";
	public static final String TRANSFORM_ON_ERROR_FUNCTION_NAME = "transformOnError";
	public static final String CLEAN_FUNCTION_NAME = "clean";

	/** Input data record used for denormalization, or <code>null</code> if not accessible. */
	protected DataRecord inputRecord = null;
	/** Output data record used for denormalization, or <code>null</code> if not accessible. */
	protected DataRecord outputRecord = null;

	@Override
	public final boolean init(Properties parameters, DataRecordMetadata sourceMetadata, DataRecordMetadata targetMetadata)
			throws ComponentNotReadyException {
		globalScopeInit();

		return initDelegate();
	}

	/**
	 * Called by {@link #init(Properties, DataRecordMetadata, DataRecordMetadata)} to perform user-specific
	 * initialization defined in the CTL transform. The default implementation does nothing, may be overridden
	 * by the generated transform class.
	 *
	 * @return <code>true</code> on success, <code>false</code> otherwise
	 *
	 * @throws ComponentNotReadyException if the initialization fails
	 */
	@CTLEntryPoint(name = INIT_FUNCTION_NAME, required = false)
	protected Boolean initDelegate() throws ComponentNotReadyException {
		// does nothing and succeeds by default, may be overridden by generated transform classes
		return true;
	}

	@Override
	public final int append(DataRecord inRecord, DataRecord outRecord) throws TransformException {
		int result = 0;

		//both input and output records are accessible within the append() function
		inputRecord = inRecord;
		outputRecord = outRecord;

		try {
			result = appendDelegate();
		} catch (ComponentNotReadyException exception) {
			// the exception may be thrown by lookups, sequences, etc.
			throw new TransformException("Generated transform class threw an exception!", exception);
		}

		// make the input record inaccessible again
		inputRecord = null;
		outputRecord = null;
		
		return result;
	}

	/**
	 * Called by {@link #append(DataRecord)} to append data record in a user-specific way defined in the CTL transform.
	 * Has to be overridden by the generated transform class.
	 *
	 * @throws ComponentNotReadyException if some internal initialization failed
	 * @throws TransformException if an error occurred
	 */
	@CTLEntryPoint(name = APPEND_FUNCTION_NAME, required = true)
	protected abstract Integer appendDelegate() throws ComponentNotReadyException, TransformException;

	@Override
	public final int appendOnError(Exception exception, DataRecord inRecord, DataRecord outRecord) throws TransformException {
		int result = 0;

		// both input and output records are accessible within the appendOnError() function
		inputRecord = inRecord;
		outputRecord = outRecord;

		try {
			result = appendOnErrorDelegate(TransformUtils.getMessage(exception), ExceptionUtils.stackTraceToString(exception));
		} catch (UnsupportedOperationException ex) {
			// no custom error handling implemented, throw an exception so the transformation fails
			throw new TransformException("Denormalization failed!", exception);
		} catch (ComponentNotReadyException ex) {
			// the exception may be thrown by lookups, sequences, etc.
			throw new TransformException("Generated transform class threw an exception!", exception);
		}

		// make the input record inaccessible again
		inputRecord = null;
		outputRecord = null;

		return result;
	}

	/**
	 * Called by {@link #appendOnError(Exception, DataRecord)} to append data record in a user-specific way defined
	 * in the CTL transform. May be overridden by the generated transform class.
	 * Throws <code>UnsupportedOperationException</code> by default.
	 *
	 * @param errorMessage an error message of the error message that occurred
	 * @param stackTrace a stack trace of the error message that occurred
	 *
	 * @throws ComponentNotReadyException if some internal initialization failed
	 * @throws TransformException if an error occurred
	 */
	@CTLEntryPoint(name = APPEND_ON_ERROR_FUNCTION_NAME, parameterNames = {
			ERROR_MESSAGE_PARAM_NAME, STACK_TRACE_PARAM_NAME }, required = false)
	protected Integer appendOnErrorDelegate(String errorMessage, String stackTrace)
			throws ComponentNotReadyException, TransformException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public final int transform(DataRecord inRecord, DataRecord outRecord) throws TransformException {
		int result = 0;

		// both input and output records are accessible within the transform() function
		inputRecord = inRecord;
		outputRecord = outRecord;

		try {
			result = transformDelegate();
		} catch (ComponentNotReadyException exception) {
			// the exception may be thrown by lookups, sequences, etc.
			throw new TransformException("Generated transform class threw an exception!", exception);
		}

		// make the input record inaccessible again
		inputRecord = null;
		outputRecord = null;

		return result;
	}

	/**
	 * Called by {@link #transform(DataRecord)} to transform data records in a user-specific way defined in the
	 * CTL transform. Has to be overridden by the generated transform class.
	 *
	 * @throws ComponentNotReadyException if some internal initialization failed
	 * @throws TransformException if an error occurred
	 */
	@CTLEntryPoint(name = TRANSFORM_FUNCTION_NAME, required = true)
	protected abstract Integer transformDelegate() throws ComponentNotReadyException, TransformException;

	@Override
	public final int transformOnError(Exception exception, DataRecord inRecord, DataRecord outRecord) throws TransformException {
		int result = 0;

		// both input and output records are accessible within the transformOnError() function
		inputRecord = inRecord;
		outputRecord = outRecord;

		try {
			result = transformOnErrorDelegate(TransformUtils.getMessage(exception), ExceptionUtils.stackTraceToString(exception));
		} catch (UnsupportedOperationException ex) {
			// no custom error handling implemented, throw an exception so the transformation fails
			throw new TransformException("Denormalization failed!", exception);
		} catch (ComponentNotReadyException ex) {
			// the exception may be thrown by lookups, sequences, etc.
			throw new TransformException("Generated transform class threw an exception!", exception);
		}

		// make the input record inaccessible again
		inputRecord = null;
		outputRecord = null;

		return result;
	}

	/**
	 * Called by {@link #transformOnError(Exception, DataRecord)} to transform data records in a user-specific
	 * way defined in the CTL transform. May be overridden by the generated transform class.
	 * Throws <code>UnsupportedOperationException</code> by default.
	 *
	 * @param errorMessage an error message of the error message that occurred
	 * @param stackTrace a stack trace of the error message that occurred
	 *
	 * @throws ComponentNotReadyException if some internal initialization failed
	 * @throws TransformException if an error occurred
	 */
	@CTLEntryPoint(name = TRANSFORM_ON_ERROR_FUNCTION_NAME, parameterNames = {
			ERROR_MESSAGE_PARAM_NAME, STACK_TRACE_PARAM_NAME }, required = false)
	protected Integer transformOnErrorDelegate(String errorMessage, String stackTrace)
			throws ComponentNotReadyException, TransformException {
		throw new UnsupportedOperationException();
	}

	@Override
	@CTLEntryPoint(name = CLEAN_FUNCTION_NAME, required = false)
	public void clean() {
		// does nothing by default, may be overridden by generated transform classes
	}

	@Override
	protected final DataRecord getInputRecord(int index) {
		if (inputRecord == null) {
			throw new TransformLangExecutorRuntimeException(INPUT_RECORDS_NOT_ACCESSIBLE);
		}

		if (index != 0) {
			throw new TransformLangExecutorRuntimeException(new Object[] { index }, INPUT_RECORD_NOT_DEFINED);
		}

		return inputRecord;
	}

	@Override
	protected final DataRecord getOutputRecord(int index) {
		if (outputRecord == null) {
			throw new TransformLangExecutorRuntimeException(OUTPUT_RECORDS_NOT_ACCESSIBLE);
		}

		if (index != 0) {
			throw new TransformLangExecutorRuntimeException(new Object[] { index }, OUTPUT_RECORD_NOT_DEFINED);
		}

		return outputRecord;
	}

	//these methods override deprecated method, see CLO-6389
	@Deprecated
	@Override
	public int append(DataRecord inRecord) throws TransformException {
		return append(inRecord, null);
	}

	@Deprecated
	@Override
	public int appendOnError(Exception exception, DataRecord inRecord) throws TransformException {
		return appendOnError(exception, inRecord, null);
	}

	@Deprecated
	@Override
	public int transform(DataRecord outRecord) throws TransformException {
		return transform(null, outRecord);
	}

	@Deprecated
	@Override
	public int transformOnError(Exception exception, DataRecord outRecord) throws TransformException {
		return transformOnError(exception, null, outRecord);
	}

}
