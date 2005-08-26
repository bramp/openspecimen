package edu.wustl.catissuecore.query;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import edu.wustl.catissuecore.dao.JDBCDAO;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.util.dbManager.DAOException;



/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright: (c) Washington University, School of Medicine 2005</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *@author Aarti Sharma
 *@version 1.0
 */
public abstract class Query {
    /**
     * Advanced query type constant
     */
    public static final String ADVANCED_QUERY = "AdvancedQuery";
    
    /**
     * Simple query type constant
     */
    public static final String SIMPLE_QUERY = "SimpleQuery";
    
    /**
     * Participant object constant
     */
    public static final String PARTICIPANT = "Participant"; 
    
    public static final String COLLECTION_PROTOCOL_REGISTRATION = "CollectionProtocolRegistration"; 
    public static final String COLLECTION_PROTOCOL = "CollectionProtocol"; 
    public static final String COLLECTION_PROTOCOL_EVENT = "CollectionProtocolEvent"; 
    public static final String SPECIMEN_COLLECTION_GROUP = "SpecimenCollectionGroup"; 
    public static final String SPECIMEN = "Specimen"; 
    
    public static final String DEPARTMENT = "Department";
    
    /**
	 * Vector of DataElement objects that need to be selected in the output
	 */
    private Vector resultView = new Vector();
	
    /**
     * Starting object from which all related objects can be part of the query
     */
    protected String queryStartObject = new String();
	
    /**
     * Parent object of the queryStartObject i.e. the object from which the
     * queryStartObject is derived 
     */
    private String parentOfQueryStartObject = new String();
	
    /**
     * Object that forms the where part of query.
     * This is SimpleConditionsImpl object in case of Simple query
     * and AdvancedConditionsImpl object in case of Advanced query
     */
    protected ConditionsImpl whereConditions;
	
    /**
     * Suffix that is appended to all table aliases which is helpful in case of nested queries
     * to differentiate between super query object from the subquery object of same type
     */
    protected int tableSufix=1;
	
	/**
	 * This method executes the query string formed from getString method and creates a temporary table.
	 * @return Returns true in case everything is successful else false
	 */
	public boolean execute() throws DAOException
	{
	    try
	    {
	        JDBCDAO dao = new JDBCDAO();
	        dao.openSession();
			List list = dao.executeQuery(getString());
	        String tableName = Constants.QUERY_RESULTS_TABLE;
	        
	        dao.delete(tableName);
	        dao.create(tableName,Constants.DEFAULT_SPREADSHEET_COLUMNS);
	        
	        Iterator iterator = list.iterator();
	        while (iterator.hasNext())
	        {
	            List row = (List) iterator.next();
	            
	            dao.insert(tableName, row);
	        }
	        
	        dao.closeSession();
	    }
	    catch(DAOException daoExp)
	    {
	        throw new DAOException(daoExp.getMessage(), daoExp);
	    }
	    catch(ClassNotFoundException classExp)
	    {
	        throw new DAOException(classExp.getMessage(), classExp);
	    }
	    return true;
	}
			/**
	 * Adds the dataElement to result view
	 * @param dataElement - Data Element to be added
	 * @return - true (as per the general contract of Collection.add).
	 */
	public boolean addElementToView(DataElement dataElement)
	{
	    return resultView.add(dataElement);
	}
	
	/**
	 * Returns the SQL representation of this query object
	 * @return
	 */
	public String getString()
	{
	    StringBuffer query= new StringBuffer();
	    
	    /**
	     * Forming SELECT part of the query
	     */
	    query.append("Select ");
	    if(resultView.size()==0)
	    {
	        query.append(" * ");
	    }
	    else
	    {
		    DataElement dataElement;
		    for(int i=0; i < resultView.size();i++)
		    {
		        dataElement = (DataElement)resultView.get(i);
		        if(i != resultView.size()-1)
		            query.append(dataElement.getTable()+tableSufix+"."+dataElement.getField()+", ");
		        else
		            query.append(dataElement.getTable()+tableSufix+"."+dataElement.getField()+" ");
		    }
	    }
	    
	    /**
	     * Forming FROM part of query
	     */
	    HashSet set = this.getQueryObjects(this.queryStartObject);
//	    for(int i = 0 ; i < resultView.size(); i++)
//	    {
//	        set.add(((DataElement)resultView.get(i)).getTable());
//	    }
	    
	    System.out.println("Set : "+ set.toString());
	    query.append("\nFROM ");
	    query.append(this.formFromString(set));
	    
	    /**
	     * Forming WHERE part of the query
	     */
	    query.append("\nWHERE ");
	    String joinConditionString = this.getJoinConditionString(set);
	    query.append(joinConditionString);
	    String whereConditionsString = whereConditions.getString(tableSufix);
	    if(whereConditionsString !=null)
	    {
	        if(joinConditionString!=null && joinConditionString.length()!=0)
	        {
	            query.append(" "+Operator.AND);
	        }
		    query.append(whereConditionsString);
		}
		return query.toString();
	}
	
	/**
	 * This method returns set of all objects related to queryStartObject transitively
     * @param string - Starting object to which all related objects should be found
     * @return set of all objects related to queryStartObject transitively
     */
    private HashSet getQueryObjects(String queryStartObject)
    {
        HashSet set = new HashSet();
        set.add(queryStartObject);
        Vector relatedObjectsCollection = (Vector)Client.relations.get(queryStartObject);
        if(relatedObjectsCollection == null)
        {
            return set;
        }
        
        set.addAll(relatedObjectsCollection);
        for(int i=0;i<relatedObjectsCollection.size();i++)
        {
             set.addAll(getQueryObjects((String) relatedObjectsCollection.get(i)));
        }
        
//        while(queryStartObject != null)
//        {
//            set.add(queryStartObject);
//            queryStartObject = (String) Client.relations.get(queryStartObject);
//        }
        return set;
    }

    /**
     * This method returns the Join Conditions string that joins all the tables/objects in the set.
     * In case its a subquery relation with the superquery is also appended in this string
     * @param set - objects in the query
     * @return - string containing all join conditions
     */
    private String getJoinConditionString( final HashSet set)
    {
        StringBuffer joinConditionString =new StringBuffer();
        Object[] tablesArray =  set.toArray();
        RelationCondition relationCondition = null;
        
        //If subquery then join with the superquery
        if(tableSufix>1)
        {
            relationCondition=(RelationCondition) Client.relationConditionsForRelatedTables.get(new Relation(this.parentOfQueryStartObject,this.queryStartObject));
           
            if(relationCondition !=null)
            {
            joinConditionString.append(" "+relationCondition.getRightDataElement().toSQLString(tableSufix));
            joinConditionString.append(Operator.EQUAL);
            joinConditionString.append(relationCondition.getRightDataElement().toSQLString(tableSufix-1)+" ");
            }
        }
        
        
        //For all permutations of tables find the joining conditions
        for(int i =0; i< tablesArray.length; i++)
        {
            for(int j=i+1; j < tablesArray.length; j++)
            {
                relationCondition=(RelationCondition) Client.relationConditionsForRelatedTables.get(new Relation((String)tablesArray[i],(String)tablesArray[j]));
                if ( relationCondition != null)
                        {
                    		if(joinConditionString.length() != 0)
                    		{
                    		    joinConditionString.append(Operator.AND+" ");
                    		}
                    		joinConditionString.append(relationCondition.toSQLString(tableSufix));
                    		                   		
                        }
                else
                {
                    relationCondition=(RelationCondition) Client.relationConditionsForRelatedTables.get(new Relation((String)tablesArray[j],(String)tablesArray[i]));
                    if ( relationCondition != null)
                    {
                		if(joinConditionString.length() != 0)
                		{
                		    joinConditionString.append(Operator.AND+" ");
                		}
                		joinConditionString.append(relationCondition.toSQLString(tableSufix));
                		                   		
                    }
                }
            }
        }
        return joinConditionString.toString();
    }


    /**
	 * This method returns the string of table names in set that forms FROM part of query
	 * which forms the FROM part of the query
	 * @param set - set of tables
	 * @return A comma separated list of the tables in the set
	 */
	private String formFromString(final HashSet set)
	{
	    StringBuffer fromString = new StringBuffer();
	    Iterator it = set.iterator();
	    Object tableAlias;
	    while(it.hasNext())
	    {
	        fromString.append(" ");
	        tableAlias = it.next();
	        fromString.append(Client.objectTableNames.get(tableAlias)+ " "+ tableAlias+tableSufix+" ");
	        if(it.hasNext())
	        {
	            fromString.append(",");
	        }
	    }
	    fromString.append(" ");
	    return fromString.toString();
	}
	
    public int getTableSufix()
    {
        return tableSufix;
    }
    
    public void setTableSufix(int tableSufix)
    {
        this.tableSufix = tableSufix;
    }
   
    public String getParentOfQueryStartObject()
    {
        return parentOfQueryStartObject;
    }
    
    public void setParentOfQueryStartObject(String parentOfQueryStartObject)
    {
        this.parentOfQueryStartObject = parentOfQueryStartObject;
    }
}