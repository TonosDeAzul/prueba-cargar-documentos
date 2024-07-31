<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Upload File</title>
</head>
<body>
    <h2>Upload File</h2>
    <form action="uploadServlet" method="post" enctype="multipart/form-data">
        <label for="title">Title:</label>
        <input type="text" id="title" name="title" required /><br />
        <label for="description">Description:</label>
        <textarea id="description" name="description" required></textarea><br />
        <input type="file" name="file" required /><br />
        <input type="submit" value="Upload" />
    </form>
    <br />
    <a href="listFilesServlet">View Uploaded Files</a>
</body>
</html>
