package com.github.kayjamlang.request;

import com.github.kayjamlang.core.Argument;
import com.github.kayjamlang.core.Type;
import com.github.kayjamlang.core.containers.ClassContainer;
import com.github.kayjamlang.executor.Context;
import com.github.kayjamlang.executor.libs.Library;
import com.github.kayjamlang.executor.libs.main.MapClass;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class RequestLibrary extends Library {

    public RequestLibrary() throws Exception {
        addFunction(new LibFunction("buildHttpQuery", (mainContext, context) -> {
            MapClass map = (MapClass) context.variables.get("query");
            Map<Object, Object> queryMap = map.getVariable((Context) map.data.get("ctx"), MapClass.FIELD_MAP);

            StringBuilder query = new StringBuilder();
            for(Map.Entry<Object, Object> entry: queryMap.entrySet()){
                query.append(query.length()==0?"?":"&")
                        .append(entry.getKey().toString())
                        .append(URLEncoder.encode(entry.getValue().toString(),
                                "UTF-8"));
            }

            return query.toString();
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
                try(BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
                    String inputLine;

                    while ((inputLine = in.readLine()) != null)
                        response.append(inputLine);
                }

                return response.toString();
            }));
        }));
    }
}
