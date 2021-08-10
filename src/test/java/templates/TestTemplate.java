package templates;

import com.venky.swf.util.ToWords;
import freemarker.cache.NullCacheStorage;
import freemarker.core.ArithmeticEngine;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import in.succinct.beckn.Time;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

public class TestTemplate {
    @BeforeClass
    public static void setup(){

    }
    @Test
    public void testDate(){
        Time time = new Time();
        time.setTimestamp(new Date());
        System.out.println(time.getInner());
    }
    @Test
    public void testTemplate() throws Exception{
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setLocalizedLookup(false);
        cfg.setWrapUncheckedExceptions(true);
        ArithmeticEngine engine = ArithmeticEngine.BIGDECIMAL_ENGINE;
        engine.setMinScale(2);
        engine.setMaxScale(2);
        cfg.setArithmeticEngine(engine);
        cfg.setCacheStorage(new NullCacheStorage()); //
        cfg.setSharedVariable("to_words",new ToWords());


        JSONObject t = new JSONObject();
        t.put("x","Hello ${user}");


        Template template = new Template("name",t.toString(),cfg);
        JSONObject model = new JSONObject();
        model.put("user","Venky");
        StringWriter w = new StringWriter();
        template.process(model,w);
        t.put("x","Hello Venky");
        Assert.assertEquals(w.toString(),t.toString());
    }


}
