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
            List<String> postTitles = (List<String>) request.getAttribute("postTitles");
            if (fileNames != null && fileIds != null && postTitles != null) {
                for (int i = 0; i < fileNames.size(); i++) {
                    String fileName = fileNames.get(i);
                    Integer fileId = fileIds.get(i);
                    String postTitle = postTitles.get(i);
                    out.println("<li>Post: " + postTitle + " - <a href='downloadFileServlet?fileId=" + fileId + "'>" + fileName + "</a></li>");
                }
            } else {
                out.println("<li>No files found</li>");
            }
        %>
    </ul>
    <a href="index.jsp">Back to Upload</a>
</body>
</html>
