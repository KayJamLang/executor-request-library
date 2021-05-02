package com.github.kayjamlang.request;

import com.github.kayjamlang.core.Type;
import com.github.kayjamlang.core.expressions.data.Argument;
import com.github.kayjamlang.executor.Context;
import com.github.kayjamlang.executor.libs.Library;
import com.github.kayjamlang.executor.libs.main.MapClass;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class RequestLibrary extends Library {

    public RequestLibrary() throws Exception {
        addFunction(new LibFunction("buildHttpQuery", Type.STRING, (mainContext, context) -> {
            MapClass map = context.getVariable("query");
            Map<Object, Object> queryMap = map.getVariable((Context) map.data.get("ctx"),
                    MapClass.FIELD_MAP);

            List<String> query = new ArrayList<>();
            for(Map.Entry<Object, Object> entry: queryMap.entrySet()){
                query.add(entry.getKey()+"="+URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
            }

            return String.join("&", query);
        }, new Argument(Type.of("map"), "query")));

        classes.put("request", new LibClass("request", (clazz)->{
            clazz.addVariable("method", "GET");
            clazz.addConstructor(new LibConstructor((mainContext, context) -> {
                    context.parentContext.variables.put("url", context.variables.get("url"));
                    context.parentContext.addVariable("body", null);
                    return null;
            }, new Argument(Type.STRING, "url")));

            clazz.addFunction(new LibFunction("execute", Type.STRING, (mainContext, context) -> {
                URL obj = new URL(context.parentContext.getVariable("url"));
                HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
                connection.setDoOutput(true);

                connection.setRequestMethod(context.parentContext.getVariable("method"));

                connection.connect();
                Object value = context.parentContext.getVariable("body");
                if(value!=null)
                    try(OutputStream os = connection.getOutputStream()) {
                        os.write(value.toString().getBytes(StandardCharsets.UTF_8));
                    }

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
