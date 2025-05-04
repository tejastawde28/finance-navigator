<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Enroll - Finance Navigator</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <style>
        html, body {
            height: 100%;
        }

        body {
            display: flex;
            flex-direction: column;
            background-color: #f8f9fa;
        }

        .enroll-container {
            margin-top: 3%;
            margin-bottom: 3%;
        }

        .enroll-form {
            padding: 5%;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 5px 8px rgba(0, 0, 0, 0.2), 0 9px 26px rgba(0, 0, 0, 0.19);
        }

        .enroll-form h3 {
            text-align: center;
            margin-bottom: 5%;
            color: #0062cc;
        }

        .form-group {
            position: relative;
            margin-bottom: 1.5rem;
            min-height: 90px;
        }

        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 500;
        }

        .form-icon {
            position: absolute;
            top: 42.5px;
            left: 10px;
            z-index: 2;
            color: #6c757d;
            font-size: 0.95rem;
        }

        .form-control-icon {
            padding-left: 2.25rem;
            position: relative;
            z-index: 1;
        }

        .error-message {
            color: red;
            font-size: 0.875rem;
            margin-top: 4px;
            display: block;
        }

        .required-field::after {
            content: " *";
            color: red;
            font-weight: bold;
        }
    </style>

</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand" href="#">
            <i class="fas fa-chart-line mr-2"></i>Finance Navigator
        </a>
    </div>
</nav>

<div class="container enroll-container">
    <div class="row justify-content-center">
        <div class="col-md-8 enroll-form">
            <h3>Portfolio Enrollment</h3>

            <!-- Alert Messages -->
            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        ${error}
                    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
            </c:if>

            <form:form action="${pageContext.request.contextPath}/enroll" method="post" modelAttribute="portfolioHolder">
                <div class="row">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="clientId" class="required-field">Client ID</label>
                            <i class="fas fa-id-card form-icon"></i>
                            <form:input path="clientId" class="form-control form-control-icon" placeholder="Client ID" required="required"/>
                            <form:errors path="clientId" cssClass="error-message" />
                            <small class="form-text text-muted">Choose a unique client ID for accessing your portfolio.</small>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="passcode" class="required-field">Passcode</label>
                            <i class="fas fa-lock form-icon"></i>
                            <form:password path="passcode" class="form-control form-control-icon" placeholder="Passcode" required="required"/>
                            <form:errors path="passcode" cssClass="error-message" />
                            <small class="form-text text-muted">Choose a secure passcode.</small>
                        </div>
                    </div>
                </div>

                <div class="form-group">
                    <label for="fullName" class="required-field">Full Name</label>
                    <i class="fas fa-user form-icon"></i>
                    <form:input path="fullName" class="form-control form-control-icon" placeholder="Full Name" required="required"/>
                    <form:errors path="fullName" cssClass="error-message" />
                </div>

                <div class="row">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="email" class="required-field">Email</label>
                            <i class="fas fa-envelope form-icon"></i>
                            <form:input path="email" type="email" class="form-control form-control-icon" placeholder="Email" required="required"/>
                            <form:errors path="email" cssClass="error-message" />
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="contactNumber">Contact Number</label>
                            <i class="fas fa-phone form-icon"></i>
                            <form:input path="contactNumber" class="form-control form-control-icon" placeholder="Contact Number"/>
                            <form:errors path="contactNumber" cssClass="error-message" />
                        </div>
                    </div>
                </div>

                <div class="form-group">
                    <button type="submit" class="btn btn-primary btn-block">Complete Enrollment</button>
                </div>
            </form:form>
            <div class="text-center mt-3">
                <p>Already have an account? <a href="<c:url value='/access'/>">Access Portfolio</a></p>
            </div>
        </div>
    </div>
</div>

<footer class="bg-dark text-white py-3 mt-auto">
    <div class="container">
        <div class="row">
            <div class="col-md-12 text-center">
                <p class="mb-0">&copy; 2025 Finance Navigator. All rights reserved.</p>
            </div>
        </div>
    </div>
</footer>

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/js/bootstrap.min.js"></script>
</body>
</html>