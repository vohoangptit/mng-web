package com.nera.nms.utils;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class CSVUtils {

    private CSVUtils() {}

    private static final char DEFAULT_SEPARATOR = ',';
    
    public static void writeLine(Writer w, List<String> values) throws IOException {
        writeLine(w, values, DEFAULT_SEPARATOR, ' ');
    }

    public static void writeLine(Writer w, List<String> values, char separators) throws IOException {
        writeLine(w, values, separators, ' ');
    }

    //https://tools.ietf.org/html/rfc4180
    private static String followCVSformat(String value) {

        String result = value;
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;

    }

    public static void writeLine(Writer w, List<String> values, char separators, char customQuote) throws IOException {

        boolean first = true;

        //default customQuote is empty

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(separators);
            }
            if (customQuote == ' ') {
                sb.append(followCVSformat(value));
            } else {
                sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
            }

            first = false;
        }
        sb.append("\n");
        w.append(sb.toString());
    }

    public static List<String> readLine(String cvsLine, char separators, char customQuote) {
        List<String> result = new ArrayList<>();

        //if empty, return!
        StringBuilder curVal = new StringBuilder();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = cvsLine.toCharArray();
        for (char ch : chars) {
            if (inQuotes) {
                startCollectChar = true;
                if (ch == customQuote) {
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                } else {
                    //Fixed : allow "" in custom quote enclosed
                    doubleQuotesInColumn = isDoubleQuotesInColumn(curVal, doubleQuotesInColumn, ch);
                }
            } else {
                if (ch == customQuote) {
                    inQuotes = true;
                    setStringBuilderByQuotes(chars[0] != '"' && customQuote == '\"', curVal, startCollectChar);
                } else if (ch == separators) {
                    result.add(curVal.toString());
                    curVal = new StringBuilder();
                    startCollectChar = false;
                } else if (ch != '\r' && setStringBuilderByDownLine(curVal, ch)) {
                    break;
                }
            }
        }
        result.add(curVal.toString());
        return result;
    }

    private static boolean setStringBuilderByDownLine(StringBuilder curVal, char ch) {
        if (ch == '\n') {
            //the end, break!
            return true;
        }
        curVal.append(ch);
        return false;
    }

    private static void setStringBuilderByQuotes(boolean checkQuotes, StringBuilder curVal, boolean startCollectChar) {
        //Fixed : allow "" in empty quote enclosed
        if (checkQuotes) {
            curVal.append('"');
        }
        //double quotes in column will hit this!
        if (startCollectChar) {
            curVal.append('"');
        }
    }

    private static boolean isDoubleQuotesInColumn(StringBuilder curVal, boolean doubleQuotesInColumn, char ch) {
        if (ch == '\"') {
            if (!doubleQuotesInColumn) {
                curVal.append(ch);
                doubleQuotesInColumn = true;
            }
        } else {
            curVal.append(ch);
        }
        return doubleQuotesInColumn;
    }


}
