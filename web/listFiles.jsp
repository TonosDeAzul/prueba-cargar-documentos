<%@page import="java.util.List"%>
<!DOCTYPE html>
<html>
<head>
    <title>Uploaded Files</title>
</head>
<body>
    <h2>Uploaded Files</h2>
    <ul>
        <%
            List<String> fileNames = (List<String>) request.getAttribute("fileNames");
            List<Integer> fileIds = (List<Integer>) request.getAttribute("fileIds");
            if (fileNames != null && fileIds != null) {
                for (int i = 0; i < fileNames.size(); i++) {
                    String fileName = fileNames.get(i);
                    Integer fileId = fileIds.get(i);
                    out.println("<li><a href='downloadFileServlet?fileId=" + fileId + "'>" + fileName + "</a></li>");
                }
            } else {
                out.println("<li>No files found</li>");
            }
        %>
    </ul>
    <a href="index.jsp">Back to Upload</a>
</body>
</html>
