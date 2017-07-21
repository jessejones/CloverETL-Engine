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
package org.jetel.database.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.jetel.data.DataRecord;
import org.jetel.exception.ConfigurationStatus;
import org.jetel.exception.JetelException;
import org.jetel.graph.Node;
import org.jetel.metadata.DataFieldMetadata;
import org.jetel.metadata.DataRecordMetadata;

/**
 * This interface represents customisation in behaviour of a JDBC connection.
 * The class parameter of jdbcSpecific extension point has to implement this interface.
 * 
 * @author Martin Zatopek (martin.zatopek@javlinconsulting.cz)
 *         (c) Javlin Consulting (www.javlinconsulting.cz)
 *
 * @created Jun 3, 2008
 */
public interface JdbcSpecific {

	/**
	 * List of all considered database operations.
	 */
	public enum OperationType {
		READ,
		WRITE,
		CALL,
		TRANSACTION,
		UNKNOWN
	}
	
	/**
	 * List of all supported retrieving types of auto-generated keys. 
	 */
	public enum AutoGeneratedKeysType {
		MULTI,
		SINGLE,
		NONE
	}
	
	/**
	 * @return unique identifier of this JdbcSpecific (should be same as the 'database' attribute in plungin.xml)
	 */
	public String getId();
	
	/**
	 * @return True if it's possible to close result set before creating new one, false otherwise.
	 */
	public boolean canCloseResultSetBeforeCreatingNewOne();

	/**
	 * @return Pattern of db field.
	 */
	public String getDbFieldPattern();
	
	/**
	 * Creates java.sql.Connection, which should follow 
	 * all specific behaviour with the given operation type.
	 * Method is intended to be overridden.
	 * @param connection
	 * @param operationType
	 * @return
	 * @throws JetelException
	 */
	public SqlConnection createSQLConnection(DBConnection dbConnection, Connection sqlConnection, OperationType operationType) throws JetelException;

	/**
	 * Performs check of metatadata if there are some special (DB specific) requirements.
	 * @param status
	 * @param metadata
	 * @param node
	 * @return
	 */
	public ConfigurationStatus checkMetadata(ConfigurationStatus status, Collection<DataRecordMetadata> metadata, Node node);
	
	/**
	 * @return type of supported auto-generated key retrieving
	 */
	public AutoGeneratedKeysType getAutoKeyType();
	
	/**
	 * Via this method, it could be a result set optimized with the given operation type.
	 * @param resultSet
	 * @param operationType
	 */
	public void optimizeResultSet(ResultSet resultSet, OperationType operationType);
	
	/**
	 * Returns whether given Clover data type can be converted to given SQL data type
	 * (some conversions are ambiguous)
	 * 
	 * @param sqlType
	 * @param field
	 * @return
	 */
	
	public boolean isJetelTypeConvertible2sql(int sqlType, DataFieldMetadata field);
	
	/**
	 * Returns whether given Sql data type can be converted to given Clover data type
	 * (some conversions are ambiguous)
	 * 
	 * @param sqlType
	 * @param field
	 * @return
	 */
	public boolean isSqlTypeConvertible2jetel(int sqlType, DataFieldMetadata field);
	
	/**
	 * This method defines a conversion table from a sql type to a clover field type.	 * 
	 * @param sqlType
	 * @param sqlPrecision
	 * @return
	 */
	public char sqlType2jetel(int sqlType, int sqlPrecision);
	
	/**
	 * This method defines a conversion table from a sql type to a clover field type.	 * 
	 * @param sqlType
	 * @return
	 */
	public char sqlType2jetel(int sqlType);
	
	/**
	 * This method defines a conversion table from a clover field type to a sql type .
	 * @param field
	 * @return
	 */
	public int jetelType2sql(DataFieldMetadata field);
	
	/**
	 * Converts field Clover metadata into SQL DDL type...
	 * e.g. for a fixed length string Clover field it returns "CHAR(15)", etc.
	 * 
	 * Similar to sqlType2str but this one is more precise as it knows more about the particular clover field
	 * 
	 * @param field
	 * @return
	 */
	public String jetelType2sqlDDL(DataFieldMetadata field);
	
	/**
	 * Creates copy object - bridge between JDBC data types and Clover data types.
	 */
	public CopySQLData createCopyObject(int SQLType, DataFieldMetadata fieldMetadata, DataRecord record, int fromIndex, int toIndex);
	
	/**
	 * @return class name where are constants with sql types
	 */
	public String getTypesClassName();
	
	/**
	 * @return constant for sql type, which will be regarded as "Result set"
	 */
	public String getResultSetParameterTypeField();

	/**
	 * @return a regex pattern matching all specific SQL comments
	 *
	 * @version 7th October 2009
	 * @since 7th October 2009
	 */
	public Pattern getCommentsPattern();

	/**
	 * Determines sql type by the type name. Intended to be used when the driver recognize the type as Types.CLOB (2005)
	 * @param sqlTypeName
	 * @return
	 */
	public int getSqlTypeByTypeName(String sqlTypeName);
	
	/**
	 * This can be used to convert java sql types into real names of a data type instide the database
	 * @return Name of database specific data type corresponding to java.sql.Types type
	 */
	public String sqlType2str(int sqlType);

	/**
	 * Quotes (escapes) a given identifier according to the database specifics.
	 *
	 * @param identifier the identifier to be quoted
	 *
	 * @return the quoted identifier
	 */
	public String quoteIdentifier(String identifier);

	/**
	 * Quotes (escapes) a given string according to the database specifics.
	 *
	 * @param identifier the string to be quoted (identifier, table name etc.)
	 *
	 * @return the quoted string
	 */
	public String quoteString(String string);
	
	/**
	 * Transforms `query` into another query, which can be used to validate the original `query`
	 * Typically somehow adds some always failing where clause so that the query is never executed
	 * @param query Original query to be validated
	 * @param queryType Type of query
	 * @param optimizeQueryString True if the select query shall be optimized by appending WHERE 1=0, false otherwise.
	 * @return A query that can be executed to validate original `query`
	 * @throws SQLException In can query cannot be generated or is otherwise invalid
	 */
	public String getValidateQuery(String query, QueryType queryType, boolean optimizeSelectQuery) throws SQLException;
	
	/**
	 * Returns whether given string is a literal in given db engine
	 * Examples:
	 * 'string' - true
	 * fieldName - false
	 * 123 - true
	 * SELECT - false
	 * , - false
	 * `name` - false
	 * etc.
	 * @return
	 */
	public boolean isLiteral(String s);
	
	/**
	 * @param statement
	 * @return True if given statement is SQL CASE statement, false otherwise.
	 */
	public boolean isCaseStatement(String statement);
	
    /**
     * Return select sql statement for given table.
     * Usually returns <code>select * from tablename</code>.
     * @param schema
     * @param owner
     * @param table
     * @return
     */
    public String compileSelectQuery4Table(String schema, String owner, String table);

	/**
	 * Returns whether schema should be explicitly set to address table correctly in given db engine. 
	 * 
	 * @return
	 */
	public boolean isSchemaRequired();
	
	/**
	 * Returns table prefix, so it can be correctly fully qualified.
	 * 
	 * @param schema - table schema
	 * @param owner - table owner
	 * @param quoteIdentifiers - identifiers will be quoted if true. 
	 * @return
	 */
	public String getTablePrefix(String schema, String owner, boolean quoteIdentifiers);
	
	/**
	 * Created as a substitute for DatabaseMetaData.supportsGetGeneratedKeys called from
	 * DBOutputTable. According to JDBC specification, DatabaseMetaData.supportsGetGeneratedKeys
	 * returns true only if multi-row inserts are supported by auto-generated keys retrieval.
	 * In contrast, this (JdbcSpecific) method returns true also for databases which can return
	 * auto-generated keys only for single-row inserts.
	 * 
	 * @param metadata - metadata of a corresponding JDBC connection
	 * @return true iff a database supports at least single-row auto-generated keys retrieval
	 * @throws SQLException
	 */
	public boolean supportsGetGeneratedKeys(DatabaseMetaData metadata) throws SQLException;
	
	/**
	 * Returns list of java.sql.Types values - types of each column in resultset
	 * @param resultSetMetadata metadata of DB ResultSet
	 * @param cloverMetadata clover metadata
	 * @return list of constants for types. Constants are from java.sql.Types
	 * @throws SQLException
	 */
	public List<Integer> getFieldTypes(ResultSetMetaData resultSetMetadata, DataRecordMetadata cloverMetadata) throws SQLException;
	
	/**
	 * Indicates whether to set SAVEPOINT after each successfully executed statement in a transaction.
	 * This method was added thanks to PostgreSQL transaction handling, where transaction is aborted if exception occurs in it.
	 * For other DBs, this method should most likely return false.
	 * @return true if SAVEPOINTs should be set after each successful statement execution in a transaction,
	 * thus enabling partial rollback to last savepoint if next statement fails.
	 */
	public boolean useSavepoints();
	
	/**
	 * Performs driver-specific operations upon
	 * driver unloading.
	 * 
	 * @param driver the driver to be unloaded
	 */
	public void unloadDriver(JdbcDriver driver);
	
	/**
	 * @param resultSet
	 * @return Implementation of result set which allows to call get methods multiple times.
	 */
	public ResultSet wrapResultSet(ResultSet resultSet);
	
	/**
	 * Returns a string which will be added at the end of CREATE TABLE statement 
	 * @param metadata
	 * @return
	 */
	public String getCreateTableSuffix(DataRecordMetadata metadata);
	
}
