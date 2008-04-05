package ie.omk.smpp.examples;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for running SMPPAPI examples.
 * @version $Id:$
 */
public class ExampleRunner {
    private static final Logger STATIC_LOG = LoggerFactory.getLogger(ExampleRunner.class);
    private Logger log;

    private Properties loadProperties() throws IOException {
        InputStream in = getClass().getClassLoader().getResourceAsStream(
                "examples.properties");
        if (in == null) {
            STATIC_LOG.error("Cannot load examples.properties.");
            return new Properties();
        }
        Properties props = new Properties();
        props.load(in);
        return props;
    }
    
    private void setPropertyValues(Object obj, Properties props) throws Exception {
        Set<Map.Entry<Object, Object>> set = props.entrySet();
        for (Iterator<Map.Entry<Object, Object>> iter = set.iterator(); iter.hasNext(); ) {
            Map.Entry<Object, Object> entry = iter.next();
            String propName = entry.getKey().toString();
            Method setter = getSetter(obj, propName);
            if (setter != null) {
                setPropertyValue(obj, setter, entry.getValue());
            }
        }
    }

    private void setPropertyValue(Object obj, Method setter, Object value) throws InvocationTargetException, IllegalAccessException {
        Class<?> argType = setter.getParameterTypes()[0];
        Object convertedValue = convertValue(value.toString(), argType);
        setter.invoke(obj, new Object[] {convertedValue});
    }
    
    private Object convertValue(String str, Class<?> type) {
        if (type == String.class) {
            return str;
        } else if (type == Integer.class || type == Integer.TYPE) {
            return Integer.parseInt(str);
        } else {
            throw new RuntimeException();
        }
    }
    
    private Method getSetter(Object obj, String propName) {
        Method setter = null;
        try {
            PropertyDescriptor descriptor =
                new PropertyDescriptor(propName, obj.getClass());
            setter = descriptor.getWriteMethod();
        } catch (IntrospectionException x) {
            log.warn("Could not set property " + propName);
        }
        return setter;
    }
    
    private SmppapiExample loadExample() throws Exception {
        String name = System.getProperty("example");
        if (name == null) {
            return null;
        }
        if (!name.startsWith("ie.omk")) {
            name = "ie.omk.smpp.examples." + name;
        }
        Class<?> type = Class.forName(name);
        return (SmppapiExample) type.newInstance();
    }
    
    private void run() throws Exception {
        SmppapiExample example = loadExample();
        if (example == null) {
            STATIC_LOG.error(
                    "You must specify an example with the -Dexample= argument");
            return;
        }
        log = LoggerFactory.getLogger(example.getClass());
        Properties props = loadProperties();
        setPropertyValues(example, props);
        example.run();
    }
    
    public static void main(String[] args) {
        try {
            ExampleRunner runner = new ExampleRunner();
            runner.run();
        } catch (Exception x) {
            STATIC_LOG.error("Stack Trace", x);
        }
    }
}
