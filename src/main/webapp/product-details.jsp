<%-- product-details.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>
<h2>${product.name}</h2>
<p>Price: $${product.price}</p>

<h3>Reviews</h3>
<c:forEach var="r" items="${reviews}">
  <p>${r.username}: ${r.comment} (${r.rating}/5)</p>
</c:forEach>
<c:if test="${empty reviews}">
  <p>No reviews yet.</p>
</c:if>

<%-- Add Review --%>
<form method="post" action="add-review">
  <input type="hidden" name="productId" value="${product.id}" />
  <textarea name="comment" placeholder="Write your review..."></textarea><br/>
  <select name="rating">
    <option value="5">5 - Excellent</option>
    <option value="4">4 - Good</option>
    <option value="3">3 - Average</option>
    <option value="2">2 - Poor</option>
    <option value="1">1 - Terrible</option>
  </select><br/>
  <button type="submit">Submit Review</button>
</form>

<a href="products">Back</a>
</body>
</html>