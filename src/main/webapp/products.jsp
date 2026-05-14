<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>

<%-- User Info --%>
<h2>Welcome, ${user}!</h2>
<c:if test="${role == 'admin'}"><p><b>[Admin]</b></p></c:if>
<a href="signout">Sign Out</a>

<%-- Error --%>
<c:if test="${not empty error}">
    <p style="color:red;">${error}</p>
</c:if>

<%-- Products --%>
<h2>All Products</h2>

<c:if test="${role == 'admin'}">
    <form method="post" action="add-product">
        <input type="text" name="name" placeholder="Product name" required/>
        <input type="number" step="0.01" name="price" placeholder="Price" required/>
        <button type="submit">Add Product</button>
    </form>
</c:if>

<c:forEach var="p" items="${products}">
    <p>
    <a href="product-details?id=${p.id}">${p.name}</a> - $${p.price}
    <c:if test="${role == 'admin'}">
        <form method="post" action="delete-product" style="display:inline">
            <input type="hidden" name="id" value="${p.id}"/>
            <button type="submit">Delete</button>
        </form>
    </c:if>
    </p>
</c:forEach>

<%-- Reviews Section --%>
<h2>Customer Reviews</h2>
<c:forEach var="r" items="${reviews}">
    <p><b>${r.username}</b>: ${r.comment} (${r.rating}/5)</p>
</c:forEach>
<c:if test="${empty reviews}">
    <p>No reviews yet.</p>
</c:if>

<%-- Delete Account --%>
<form method="post" action="delete-account">
    <button type="submit"
            onclick="return confirm('Are you sure?')">
        Delete My Account
    </button>
</form>

</body>
</html>