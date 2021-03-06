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

package co.freeside.betamax.tape

import co.freeside.betamax.ProxyRecorder
import co.freeside.betamax.junit.RecorderRule
import com.google.common.io.Files
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.*
import org.junit.Rule
import org.yaml.snakeyaml.Yaml
import spock.lang.*
import static co.freeside.betamax.TapeMode.WRITE_ONLY
import static java.util.concurrent.TimeUnit.SECONDS

@Issue('https://github.com/robfletcher/betamax/issues/50')
@IgnoreIf({
    def url = "http://httpbin.org/".toURL()
    try {
        HttpURLConnection connection = url.openConnection()
        connection.requestMethod = "HEAD"
        connection.connectTimeout = SECONDS.toMillis(2)
        connection.connect()
        return connection.responseCode >= 400
    } catch (IOException e) {
        System.err.println "Skipping spec as $url is not available"
        return true
    }
})
class PostBodySpec extends Specification {

    @Shared @AutoCleanup('deleteDir') def tapeRoot = Files.createTempDir()
    def recorder = new ProxyRecorder(tapeRoot: tapeRoot)
    @Rule RecorderRule recorderRule = new RecorderRule(recorder)

    private DefaultHttpClient httpClient = new SystemDefaultHttpClient()

    void 'post body is stored on tape when using UrlConnection'() {
        given:
        def postBody = '{"foo":"bar"}'
        HttpURLConnection connection = 'http://httpbin.org/post'.toURL().openConnection()
        connection.doOutput = true
        connection.requestMethod = 'POST'
        connection.addRequestProperty('Content-Type', 'application/json')

        and:
        recorder.start('post_body_with_url_connection', [mode: WRITE_ONLY])

        when:
        connection.outputStream.withStream { stream ->
            stream << postBody.getBytes('UTF-8')
        }
        println connection.inputStream.text // response body must be consumed

        and:
        recorder.stop()

        then:
        def file = new File(tapeRoot, 'post_body_with_url_connection.yaml')
        def tapeData = file.withReader {
            new Yaml().loadAs(it, Map)
        }
        tapeData.interactions[0].request.body == postBody
    }

    void 'post body is stored on tape when using HttpClient'() {
        given:
        def postBody = '{"foo":"bar"}'
        def httpPost = new HttpPost('http://httpbin.org/post')
        httpPost.setHeader('Content-Type', 'application/json')
        def reqEntity = new StringEntity(postBody, 'UTF-8')
        reqEntity.setContentType('application/json')
        httpPost.entity = reqEntity

        and:
        recorder.start('post_body_with_http_client', [mode: WRITE_ONLY])

        when:
        httpClient.execute(httpPost)

        and:
        recorder.stop()

        then:
        def file = new File(tapeRoot, 'post_body_with_http_client.yaml')
        def tapeData = file.withReader {
            new Yaml().loadAs(it, Map)
        }
        tapeData.interactions[0].request.body == postBody
    }

}
