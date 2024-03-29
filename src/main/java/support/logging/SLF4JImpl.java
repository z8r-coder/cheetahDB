/*
 * Modified from com.alibaba.druid
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package support.logging;

import parser.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

import java.util.List;

public class SLF4JImpl implements Log {

    private static final String callerFQCN = SLF4JImpl.class.getName();
    private static final Logger testLogger = LoggerFactory.getLogger(SLF4JImpl.class);
    static {
        // if the logger is not a LocationAwareLogger instance, it can not get correct stack StackTraceElement
        // so ignore this implementation.
        if (!(testLogger instanceof LocationAwareLogger)) {
            throw new UnsupportedOperationException(testLogger.getClass() + " is not a suitable logger");
        }
    }
    private int                 errorCount;
    private int                 warnCount;
    private int                 infoCount;
    private int                 debugCount;
    private LocationAwareLogger log;

    public SLF4JImpl(LocationAwareLogger log){
        this.log = log;
    }

    public SLF4JImpl(String loggerName){
        this.log = (LocationAwareLogger) LoggerFactory.getLogger(loggerName);
    }

    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    public void error(String msg, String param, Throwable e) {
        error(msg, e);
        System.err.print(param);
    }

    public void error(String msg, Throwable e) {
        log.log(null, callerFQCN, LocationAwareLogger.ERROR_INT, msg, null, e);
        errorCount++;
    }

    public void error(String msg, String para) {
        error(msg);
        System.err.println(para);
    }

    public void error(String msg) {
        log.log(null, callerFQCN, LocationAwareLogger.ERROR_INT, msg, null, null);
        errorCount++;
    }

    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    public void info(String msg) {
        infoCount++;
        log.log(null, callerFQCN, LocationAwareLogger.INFO_INT, msg, null, null);
    }

    public void info(List<Token> tokens) {
        System.out.print("[");
        for (Token token : tokens) {
            System.out.print(token.getValue() + " ");
        }
        System.out.println("]");
    }

    public void info(String msg, List<Token> tokens) {
        log.log(null, callerFQCN, LocationAwareLogger.INFO_INT, msg, null, null);
        infoCount++;
        System.out.print(msg);
        info(tokens);
    }

    public void debug(String msg) {
        debugCount++;
        log.log(null, callerFQCN, LocationAwareLogger.DEBUG_INT, msg, null, null);
    }

    public void debug(String msg, Throwable e) {
        debugCount++;
        log.log(null, callerFQCN, LocationAwareLogger.ERROR_INT, msg, null, e);
    }

    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    public void warn(String msg) {
        log.log(null, callerFQCN, LocationAwareLogger.WARN_INT, msg, null, null);
        warnCount++;
    }

    public void warn(String msg, Throwable e) {
        log.log(null, callerFQCN, LocationAwareLogger.WARN_INT, msg, null, e);
        warnCount++;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public int getWarnCount() {
        return warnCount;
    }

    public int getInfoCount() {
        return infoCount;
    }

    public int getDebugCount() {
        return debugCount;
    }

    public void resetStat() {
        errorCount = 0;
        warnCount = 0;
        infoCount = 0;
        debugCount = 0;
    }

}
