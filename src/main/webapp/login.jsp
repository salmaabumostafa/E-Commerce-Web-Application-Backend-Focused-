<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<body>
<h2>Login</h2>

<% if ("invalid".equals(request.getParameter("error"))) { %>
<p style="color:red;">Invalid username or password.</p>
<% } %>

<form method="post" action="login">
    <input type="text" name="username" placeholder="Username" required /><br/>
    <input type="password" name="password" placeholder="Password" required /><br/>
    <button type="submit">Login</button>
</form>

<a href="signup.jsp">Don't have an account? Sign Up</a>
</body>
</html>