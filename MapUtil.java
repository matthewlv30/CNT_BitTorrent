import java.util.*;

/**
 * This class is for utility functions concerning Map objects.
 * Adapted from:
 * 		http://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values-java
 *
 */
public class MapUtil
{
	/**
	 * This is a generic function for sorting a Map by its value in descending order.
	 * @param	map:	This is the Map passed in to sort
	 * @return  result:	This is the sorted Map
	 */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map ) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>( map.entrySet() );
        
        Collections.sort( list, new Comparator<Map.Entry<K, V>>() {
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 ) {
                return (o2.getValue()).compareTo( o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        
        for (Map.Entry<K, V> entry : list) {
        	result.put( entry.getKey(), entry.getValue() );
        }
        
        return result;
    }
}