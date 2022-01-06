package in.succinct.beckn.portal.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DomainMapper {
    private static final Map<String,String> domainMapping = new HashMap<String,String>(){{
        put("nic2004:52110","local-retail");
        put("nic2004:55204","local-delivery");
        put("nic2004:85110","health-care");
    }};
    public static Set<String> domains(){
        return domainMapping.keySet();
    }
    public static String getMapping(String domainType){
        String mapping = domainMapping.get(domainType) ;

        return mapping == null ? domainType : mapping;
    }
}
