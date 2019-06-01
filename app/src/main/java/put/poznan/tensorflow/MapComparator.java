package put.poznan.tensorflow;

import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by SHAJIB on 7/12/2017.
 */

class MapComparator implements Comparator<HashMap<String, String>>
{
    private final String key;
    private final String order;

    public MapComparator(String key, String order)
    {
        this.key = key;
        this.order = order;
    }

    public int compare(HashMap<String, String> first,
                       HashMap<String, String> second)
    {
        // TODO: Null checking, both for maps and values
        String firstValue = first.get(key);
        String secondValue = second.get(key);
        if (key.equals(Function.KEY_COUNT) || key.equals(Function.KEY_TIMESTAMP)) {
            if(this.order.toLowerCase().contentEquals("asc"))
            {
                return Integer.parseInt(firstValue) - Integer.parseInt(secondValue);
            }else{
                return Integer.parseInt(secondValue) - Integer.parseInt(firstValue);
            }
        }
        else {
            if(this.order.toLowerCase().contentEquals("asc"))
            {
                return firstValue.compareTo(secondValue);
            }else{
                return secondValue.compareTo(firstValue);
            }

        }
    }
}