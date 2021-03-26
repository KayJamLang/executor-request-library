package com.github.kayjamlang.request;

import com.github.kayjamlang.core.Argument;
import com.github.kayjamlang.core.Type;
import com.github.kayjamlang.core.containers.ClassContainer;
import com.github.kayjamlang.executor.Context;
import com.github.kayjamlang.executor.libs.Library;
import com.github.kayjamlang.executor.libs.main.MapClass;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class RequestLibrary extends Library {

    public RequestLibrary() throws Exception {
        addFunction(new LibFunction("buildHttpQuery", (mainContext, context) -> {
            MapClass map = (MapClass) context.variables.get("query");
            Map<Object, Object> queryMap = map.getVariable((Context) map.data.get("ctx"), MapClass.FIELD_MAP);

            List<String> query = new ArrayList<>();
            for(Map.Entry<Object, Object> entry: queryMap.entrySet()){
                query.add(entry.getKey()+"="+URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
            }

            return String.join("&", query);
        }, new Argument(new Type("map", ClassContainer.class, false),
                "query")));

        classes.put("request", new LibClass("request", (clazz)->{
            clazz.addVariable("method", "GET");
            clazz.addConstructor(new LibConstructor((mainContext, context) -> {
                    context.parentContext.variables.put("url", context.variables.get("url"));
                    return null;
            }, new Argument(Type.STRING, "url")));

            clazz.addFunction(new LibFunction("execute", (mainContext, context) -> {
                URL obj = new URL((String) context.parentContext.variables.get("url"));
                HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

                connection.setRequestMethod((String) context.parentContext.variables.get("method"));

                StringBuilder response = new StringBuilder();
                InputStream inputStream = connection.getResponseCode()==200?
                        connection.getInputStream()
                        :connection.getErrorStream();
                try(Scanner scanner = new Scanner(inputStream, "UTF-8")){

                    while (scanner.hasNextLine())
                        response.append(scanner.nextLine());
                }

                return response.toString();
            }));
        }));
    }
}
