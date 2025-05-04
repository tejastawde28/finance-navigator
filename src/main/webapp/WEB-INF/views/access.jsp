<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Access Your Portfolio - Finance Navigator</title>
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

        .login-container {
            margin-top: 5%;
            margin-bottom: 5%;
        }

        .login-form {
            padding: 5%;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 5px 8px 0 rgba(0, 0, 0, 0.2), 0 9px 26px 0 rgba(0, 0, 0, 0.19);
        }

        .login-form h3 {
            text-align: center;
            margin-bottom: 12%;
            color: #0062cc;
        }

        .form-group {
            position: relative;
            margin-bottom: 1.5rem;
        }

        .form-icon {
            position: absolute;
            top: 10px;
            left: 10px;
            color: #495057;
        }

        .form-control-icon {
            padding-left: 35px;
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

<div class="container login-container">
    <div class="row justify-content-center">
        <div class="col-md-6 login-form">
            <h3>Access Your Portfolio</h3>

            <!-- Alert Messages -->
            <c:if test="${not empty success}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                        ${success}
                    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        ${error}
                    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
            </c:if>

            <form action="<c:url value='/access'/>" method="post">
                <div class="form-group">
                    <i class="fas fa-user form-icon"></i>
                    <input type="text" name="clientId" class="form-control form-control-icon ${clientIdError != null ? 'is-invalid' : ''}"
                           placeholder="Client ID" value="${clientId}" required>
                    <c:if test="${clientIdError != null}">
                        <div class="invalid-feedback">${clientIdError}</div>
                    </c:if>
                </div>
                <div class="form-group">
                    <i class="fas fa-lock form-icon"></i>
                    <input type="password" name="passcode" class="form-control form-control-icon ${passcodeError != null ? 'is-invalid' : ''}"
                           placeholder="Passcode" required>
                    <c:if test="${passcodeError != null}">
                        <div class="invalid-feedback">${passcodeError}</div>
                    </c:if>
                </div>
                <div class="form-group">
                    <button type="submit" class="btn btn-primary btn-block">Access Portfolio</button>
                </div>
            </form>
            <div class="text-center">
                <p>Don't have an account? <a href="<c:url value='/enroll'/>">Enroll Now</a></p>
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