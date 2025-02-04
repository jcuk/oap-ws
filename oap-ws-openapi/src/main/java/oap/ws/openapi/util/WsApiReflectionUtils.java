/*
 * The MIT License (MIT)
 *
 * Copyright (c) Open Application Platform Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package oap.ws.openapi.util;

import oap.http.server.nio.HttpServerExchange;
import oap.reflect.Reflection;
import oap.ws.WsParam;

import java.lang.reflect.Modifier;

import static oap.ws.WsParam.From.QUERY;
import static oap.ws.WsParam.From.SESSION;

/**
 * Util class for working with Reflection in order to extract and filter ws api data
 */
public class WsApiReflectionUtils {

    /**
     * Filter methods which are not used for processing api requests
     * @param m - reflection method data
     * @return true if method is used for request handling
     */
    public static boolean filterMethod( Reflection.Method m ) {
        return !m.underlying.getDeclaringClass().equals( Object.class )
            && !m.underlying.isSynthetic()
            && !Modifier.isStatic( m.underlying.getModifiers() )
            && m.isPublic();
    }

    /**
     * Filter parameters which are not user for incoming request parameters
     * @param parameter - reflection method parameter data
     * @return true if method parameter is used for incoming request parameter
     */
    public static boolean filterParameter( Reflection.Parameter parameter ) {
        return parameter.findAnnotation( WsParam.class )
            .map( wsp -> wsp.from() != SESSION )
            .orElse( true )
            && !parameter.type().assignableTo( HttpServerExchange.class );
    }

    /**
     * Retrieves from type of request parameter
     * @see WsParam.From
     * @param p - reflection method parameter data
     * @return - from type of request parameter
     */
    public static String from( Reflection.Parameter p ) {
        return p.findAnnotation( WsParam.class ).map( WsParam::from )
            .orElse( QUERY ).name().toLowerCase();
    }

    /**
     * Retrieves a description from request parameter
     * @see WsParam.description
     * @param p - reflection method parameter data
     * @return - string description for parameter
     */
    public static String description( Reflection.Parameter p ) {
        return p.findAnnotation( WsParam.class ).map( WsParam::description ).orElse( "" );
    }

    /**
     * Retrieves tag name for service
     *
     * @param reflection - reflection class data
     * @return tag name from annotate class or context path if empty
     */
    public static String tag( Reflection reflection ) {
        return reflection.getType().getTypeName();
    }
}
