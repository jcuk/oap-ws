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

package oap.ws.interceptor;

import lombok.extern.slf4j.Slf4j;
import oap.http.HttpResponse;
import oap.http.Request;
import oap.reflect.Reflection;
import oap.util.Stream;
import oap.ws.Session;
import org.apache.commons.collections4.iterators.ReverseListIterator;

import java.util.List;
import java.util.Optional;

@Slf4j
public class Interceptors {
    public static Optional<HttpResponse> before( List<Interceptor> interceptors, Request request, Session session, Reflection.Method method ) {
        for( var interceptor : interceptors ) {
            log.trace( "running before call {}", interceptor.getClass().getSimpleName() );
            var response = interceptor.before( request, session, method );
            if( response.isPresent() ) return response;
        }
        return Optional.empty();
    }

    public static HttpResponse after( List<Interceptor> interceptors, HttpResponse response, Session session ) {
        return Stream.of( new ReverseListIterator<>( interceptors ) )
            .foldLeft( response, ( r, interceptor ) -> {
                log.trace( "running after call {}", interceptor.getClass().getSimpleName() );
                return interceptor.after( r, session );
            } );
    }
}
