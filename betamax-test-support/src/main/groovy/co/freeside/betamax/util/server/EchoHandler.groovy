/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.freeside.betamax.util.server

import co.freeside.betamax.util.server.internal.ExceptionHandlingHandlerAdapter
import io.netty.channel.*
import io.netty.handler.codec.http.*
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE
import static io.netty.handler.codec.http.HttpResponseStatus.OK
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1

@ChannelHandler.Sharable
class EchoHandler extends ExceptionHandlingHandlerAdapter {

    @Override
    void channelRead(ChannelHandlerContext ctx, Object msg) {
        def response = new DefaultFullHttpResponse(
                HTTP_1_1,
                OK,
                ((FullHttpRequest) msg).content()
        )
        response.headers().set(CONTENT_TYPE, "text/plain charset=UTF-8")
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
    }


}
