import api.SayHelloService;
import org.apache.dubbo.common.extension.ExtensionLoader;

import java.util.ServiceLoader;
import java.util.Set;

/**
 * @Description: Main
 * @Author: 一方通行
 * @Date: 2021-08-01
 * @Version:v1.0
 */
public class Main {
    public static void main(String[] args) {
        ExtensionLoader<SayHelloService> sayHelloServiceExtensionLoader
                = ExtensionLoader.getExtensionLoader(SayHelloService.class);
        Set<String> extensions = sayHelloServiceExtensionLoader.getLoadedExtensions();
        for(String extension : extensions){
            String s = sayHelloServiceExtensionLoader.getExtension(extension).sayHello();
            System.out.println(s);
        }
        //ServiceLoader.load();
    }
}
