<%-- signup.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<body>
<h2>Sign Up</h2>

<% if ("exists".equals(request.getParameter("error"))) { %>
<p style="color:red;">Username already exists. Please choose another.</p>
<% } %>
<% if ("server".equals(request.getParameter("error"))) { %>
<p style="color:red;">Server error. Please try again.</p>
<% } %>

<form method="post" action="signup">
    <input type="text" name="username" placeholder="Username" required /><br/>
    <input type="password" name="password" placeholder="Password" required /><br/>
    <button type="submit">Sign Up</button>
</form>

<a href="login.jsp">Already have an account? Login</a>
</body>
</html>