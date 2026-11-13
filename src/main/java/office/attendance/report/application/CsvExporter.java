package office.attendance.report.application;


import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CsvExporter {

    public String toCsv(List<String> header, List<List<String>> rows) {
        StringBuilder sb = new StringBuilder("\uFEFF");   // BOM, so Excel reads UTF-8 names correctly
        appendRow(sb, header);
        for (List<String> row : rows) {
            appendRow(sb, row);
        }
        return sb.toString();
    }

    private void appendRow(StringBuilder sb, List<String> cells) {
        for (int i = 0; i < cells.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(escape(cells.get(i)));
        }
        sb.append("\r\n");
    }

    static String escape(String value) {
        String v = value == null ? "" : value;
        // Stops spreadsheet formula injection from a name such as "=HYPERLINK(...)".
        if (!v.isEmpty() && "=+-@\t\r".indexOf(v.charAt(0)) >= 0) {
            v = "'" + v;
        }
        boolean quote = v.contains(",") || v.contains("\"") || v.contains("\n") || v.contains("\r");
        v = v.replace("\"", "\"\"");
        return quote ? "\"" + v + "\"" : v;
    }
}
