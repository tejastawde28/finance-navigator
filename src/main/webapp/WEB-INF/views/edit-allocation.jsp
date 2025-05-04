<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Allocation - Finance Navigator</title>
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
                <a href="<c:url value='/allocations'/>" class="list-group-item list-group-item-action active">
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
                    <h1 class="h3 mb-0 text-gray-800">Edit Allocation</h1>
                    <a href="<c:url value='/allocations'/>" class="btn btn-sm btn-secondary">
                        <i class="fas fa-arrow-left fa-sm text-white-50 mr-1"></i> Back to Allocations
                    </a>
                </div>

                <div class="row">
                    <div class="col-lg-6 mx-auto">
                        <div class="card shadow mb-4">
                            <div class="card-header py-3">
                                <h6 class="m-0 font-weight-bold text-primary">Edit Allocation Details</h6>
                            </div>
                            <div class="card-body">
                                <form:form action="${pageContext.request.contextPath}/allocations/edit/${allocation.id}"
                                           method="post" modelAttribute="allocation">
                                    <form:hidden path="id"/>

                                    <div class="form-group">
                                        <label for="name" class="required-field">Name</label>
                                        <form:input path="name" class="form-control" id="name" placeholder="Enter allocation name" required="required"/>
                                        <form:errors path="name" cssClass="error-message" />
                                    </div>

                                    <div class="form-group">
                                        <label for="description">Description</label>
                                        <form:textarea path="description" class="form-control" id="description" rows="3" placeholder="Enter description"/>
                                        <form:errors path="description" cssClass="error-message" />
                                    </div>

                                    <div class="form-group">
                                        <label for="instrumentType" class="required-field">Type</label>
                                        <form:select path="instrumentType" class="form-control" id="instrumentType" required="required">
                                            <form:option value="" label="-- Select Type --"/>
                                            <c:forEach items="${instrumentTypes}" var="type">
                                                <form:option value="${type}" label="${type.displayName}"/>
                                            </c:forEach>
                                        </form:select>
                                        <form:errors path="instrumentType" cssClass="error-message" />
                                    </div>

                                    <div class="form-group">
                                        <label for="iconClass">Icon (Optional)</label>
                                        <div class="input-group">
                                            <div class="input-group-prepend">
                                                <span class="input-group-text"><i class="fas fa-icons"></i></span>
                                            </div>
                                            <form:input path="iconClass" class="form-control" id="iconClass" placeholder="fas fa-tag, fas fa-money-bill, etc."/>
                                        </div>
                                        <form:errors path="iconClass" cssClass="error-message" />
                                        <small class="form-text text-muted">
                                            Enter a FontAwesome icon class (e.g., fas fa-home, fas fa-car).
                                            <a href="https://fontawesome.com/v5/search?ic=free" target="_blank">Browse icons</a>
                                        </small>
                                    </div>

                                    <div class="form-group" style="min-height: auto;">
                                        <button type="submit" class="btn btn-primary">Update Allocation</button>
                                        <a href="<c:url value='/allocations'/>" class="btn btn-secondary">Cancel</a>
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
