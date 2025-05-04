<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Profile - Finance Navigator</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <style>
        .sidebar {
            min-height: calc(100vh - 56px);
            background-color: #343a40;
        }
        .sidebar .nav-link {
            color: rgba(255, 255, 255, 0.75);
        }
        .sidebar .nav-link:hover {
            color: rgba(255, 255, 255, 1);
        }
        .sidebar .nav-link.active {
            color: #fff;
            background-color: rgba(255, 255, 255, 0.1);
        }
        .content {
            padding: 20px;
        }
        .form-group {
            position: relative;
            min-height: 75px;
        }
        .required-field::after {
            content: " *";
            color: red;
            font-weight: bold;
        }
        .error-message {
            color: red;
            font-size: 0.875rem;
            position: absolute;
            bottom: 0;
        }
    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <a class="navbar-brand" href="<c:url value='/dashboard'/>">
        <i class="fas fa-chart-line mr-2"></i>Finance Navigator
    </a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav"
            aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav ml-auto">
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button"
                   data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <i class="fas fa-user-circle mr-1"></i>${portfolioHolder.fullName}
                </a>
                <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userDropdown">
                    <a class="dropdown-item" href="<c:url value='/profile'/>">
                        <i class="fas fa-id-card mr-2"></i>Profile
                    </a>
                    <div class="dropdown-divider"></div>
                    <a class="dropdown-item" href="<c:url value='/signout'/>">
                        <i class="fas fa-sign-out-alt mr-2"></i>Sign Out
                    </a>
                </div>
            </li>
        </ul>
    </div>
</nav>

<div class="container-fluid">
    <div class="row">
        <div class="col-md-2 d-none d-md-block sidebar py-3">
            <div class="list-group">
                <a href="<c:url value='/dashboard'/>" class="list-group-item list-group-item-action">
                    <i class="fas fa-tachometer-alt mr-2"></i>Dashboard
                </a>
                <a href="<c:url value='/ledger'/>" class="list-group-item list-group-item-action">
                    <i class="fas fa-book mr-2"></i>Ledger Entries
                </a>
                <a href="<c:url value='/allocations'/>" class="list-group-item list-group-item-action">
                    <i class="fas fa-tags mr-2"></i>Allocations
                </a>
                <a href="<c:url value='/report'/>" class="list-group-item list-group-item-action">
                    <i class="fas fa-chart-bar mr-2"></i>Reports
                </a>
            </div>
        </div>

        <main role="main" class="col-md-10 ml-sm-auto px-4 content">
            <!-- Alert Messages -->
            <c:if test="${not empty success}">
                <div class="alert alert-success alert-dismissible fade show mt-3" role="alert">
                        ${success}
                    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show mt-3" role="alert">
                        ${error}
                    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
            </c:if>

            <div class="container-fluid py-4">
                <div class="d-sm-flex align-items-center justify-content-between mb-4">
                    <h1 class="h3 mb-0 text-gray-800">Profile Management</h1>
                </div>

                <div class="row">
                    <div class="col-lg-8 mx-auto">
                        <div class="card shadow mb-4">
                            <div class="card-header py-3">
                                <h6 class="m-0 font-weight-bold text-primary">Portfolio Holder Details</h6>
                            </div>
                            <div class="card-body">
                                <form:form action="${pageContext.request.contextPath}/profile/update" method="post" modelAttribute="portfolioHolder">

                                    <div class="form-group row">
                                        <label for="clientId" class="col-sm-3 col-form-label">Client ID</label>
                                        <div class="col-sm-9">
                                            <input type="text" readonly class="form-control-plaintext" id="clientId"
                                                   value="${portfolioHolder.clientId}">
                                        </div>
                                    </div>

                                    <div class="form-group row">
                                        <label for="creationDate" class="col-sm-3 col-form-label">Account Created</label>
                                        <div class="col-sm-9">
                                            <input type="text" readonly class="form-control-plaintext" id="creationDate"
                                                   value="<fmt:formatDate pattern='MM/dd/yyyy' value='${portfolioHolder.creationDate}' />">
                                        </div>
                                    </div>

                                    <div class="form-group row">
                                        <label for="lastAccessed" class="col-sm-3 col-form-label">Last Access</label>
                                        <div class="col-sm-9">
                                            <input type="text" readonly class="form-control-plaintext" id="lastAccessed"
                                                   value="<fmt:formatDate pattern='MM/dd/yyyy hh:mm a' value='${portfolioHolder.lastAccessed}' />">
                                        </div>
                                    </div>

                                    <hr class="my-4">

                                    <div class="form-group row">
                                        <label for="fullName" class="col-sm-3 col-form-label required-field">Full Name</label>
                                        <div class="col-sm-9">
                                            <form:input path="fullName" class="form-control" id="fullName" required="required" placeholder="Full Name"/>
                                            <form:errors path="fullName" cssClass="error-message" />
                                        </div>
                                    </div>

                                    <div class="form-group row">
                                        <label for="email" class="col-sm-3 col-form-label required-field">Email</label>
                                        <div class="col-sm-9">
                                            <form:input path="email" type="email" class="form-control" id="email" required="required" placeholder="Email"/>
                                            <form:errors path="email" cssClass="error-message" />
                                        </div>
                                    </div>

                                    <div class="form-group row">
                                        <label for="contactNumber" class="col-sm-3 col-form-label">Contact Number</label>
                                        <div class="col-sm-9">
                                            <form:input path="contactNumber" class="form-control" id="contactNumber" placeholder="Contact Number"/>
                                            <form:errors path="contactNumber" cssClass="error-message" />
                                        </div>
                                    </div>

                                    <hr class="my-4">

                                    <div class="form-group row">
                                        <label for="passcode" class="col-sm-3 col-form-label">New Passcode</label>
                                        <div class="col-sm-9">
                                            <form:password path="passcode" class="form-control" id="passcode" placeholder="Enter only if changing"/>
                                            <form:errors path="passcode" cssClass="error-message" />
                                            <small class="form-text text-muted">
                                                Leave blank to keep your current passcode.
                                            </small>
                                        </div>
                                    </div>

                                    <div class="form-group row">
                                        <div class="col-sm-9 offset-sm-3">
                                            <button type="submit" class="btn btn-primary">Update Profile</button>
                                            <a href="<c:url value='/dashboard'/>" class="btn btn-secondary">Cancel</a>
                                        </div>
                                    </div>

                                </form:form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </main>
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
