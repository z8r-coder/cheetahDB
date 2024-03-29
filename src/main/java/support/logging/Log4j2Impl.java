package support.logging;

import parser.Token;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

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
public class Log4j2Impl implements Log {

    private Logger log;

    private int    errorCount;
    private int    warnCount;
    private int    infoCount;
    private int    debugCount;

    /**
     * @since 0.2.21
     * @param log
     */
    public Log4j2Impl(Logger log){
        this.log = log;
    }

    public Log4j2Impl(String loggerName){
        log = LogManager.getLogger(loggerName);
    }

    public Logger getLog() {
        return log;
    }

    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    public void error(String msg, String param, Throwable e) {
        error(msg,e);
        System.err.print(param);
    }

    public void error(String s, Throwable e) {
        errorCount++;
        log.error(s, e);
    }

    public void error(String msg, String para) {
        error(msg);
        System.err.println(para);
    }

    public void error(String s) {
        errorCount++;
        log.error(s);
    }

    public void debug(String s) {
        debugCount++;
        log.debug(s);
    }

    public void debug(String s, Throwable e) {
        debugCount++;
        log.debug(s, e);
    }

    public void warn(String s) {
        log.warn(s);
        warnCount++;
    }

    public void warn(String s, Throwable e) {
        log.warn(s, e);
        warnCount++;
    }

    public int getWarnCount() {
        return warnCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void resetStat() {
        errorCount = 0;
        warnCount = 0;
        infoCount = 0;
        debugCount = 0;
    }

    public int getDebugCount() {
        return debugCount;
    }

    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    public void info(String msg) {
        infoCount++;
        log.info(msg);
    }
    public void info(List<Token> tokens) {
        System.out.print("[");
        for (Token token : tokens) {
            System.out.print(token.getValue() + " ");
        }
        System.out.println("]");
    }

    public void info(String msg, List<Token> tokens) {
        log.info(msg);
        infoCount++;
        System.out.print(msg);
        info(tokens);
    }

    public boolean isWarnEnabled() {
        return log.isEnabled(Level.WARN);
    }
    
    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    public int getInfoCount() {
        return infoCount;
    }

    public String toString() {
        return log.toString();
    }

}
