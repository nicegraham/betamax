/*
 * Copyright 2013 the original author or authors.
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

package co.freeside.betamax

import co.freeside.betamax.junit.*
import org.junit.ClassRule
import spock.lang.*
import static java.net.HttpURLConnection.HTTP_OK
import static org.apache.http.HttpHeaders.VIA

@Ignore("site is no longer there")
@Issue("https://github.com/robfletcher/betamax/issues/52")
@Betamax(tape = "ocsp")
class OCSPSpec extends Specification {

    @Shared Recorder recorder = new ProxyRecorder(sslSupport: true)
    @Shared @ClassRule RecorderRule recorderRule = new RecorderRule(recorder)

    void "OCSP messages"() {
        when:
        HttpURLConnection connection = uri.toURL().openConnection()
        connection.requestMethod = "POST"

        then:
        connection.responseCode == HTTP_OK
        connection.getHeaderField(VIA) == "Betamax"
        connection.inputStream.bytes.length == 2529

        where:
        uri = "http://ocsp.ocspservice.com/public/ocsp"
    }
}
