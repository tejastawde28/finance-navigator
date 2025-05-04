<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Allocations - Finance Navigator</title>
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
                    <h1 class="h3 mb-0 text-gray-800">Allocation Management</h1>
                    <a href="<c:url value='/allocations/add'/>"
                       class="d-sm-inline-block btn btn-sm btn-primary shadow-sm">
                        <i class="fas fa-plus fa-sm text-white-50 mr-1"></i> Add New Allocation
                    </a>
                </div>

                <div class="row">
                    <!-- Asset Allocations -->
                    <div class="col-lg-6">
                        <div class="card shadow mb-4">
                            <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                                <h6 class="m-0 font-weight-bold text-success">
                                    <i class="fas fa-arrow-up mr-1"></i> Asset Allocations
                                </h6>
                            </div>
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table class="table table-bordered table-striped" width="100%" cellspacing="0">
                                        <thead>
                                        <tr>
                                            <th>Name</th>
                                            <th>Description</th>
                                            <th>Actions</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach items="${assetAllocations}" var="allocation">
                                            <tr>
                                                <td>
                                                    <c:if test="${not empty allocation.iconClass}">
                                                        <i class="${allocation.iconClass} mr-1"></i>
                                                    </c:if>
                                                        ${allocation.name}
                                                </td>
                                                <td>${allocation.description}</td>
                                                <td>
                                                    <a href="<c:url value='/allocations/edit/${allocation.id}'/>"
                                                       class="btn btn-sm btn-warning">
                                                        <i class="fas fa-edit"></i>
                                                    </a>
                                                    <a href="#" class="btn btn-sm btn-danger" data-toggle="modal"
                                                       data-target="#deleteModal${allocation.id}">
                                                        <i class="fas fa-trash"></i>
                                                    </a>

                                                    <!-- Delete Modal -->
                                                    <div class="modal fade" id="deleteModal${allocation.id}"
                                                         tabindex="-1" role="dialog"
                                                         aria-labelledby="deleteModalLabel${allocation.id}"
                                                         aria-hidden="true">
                                                        <div class="modal-dialog" role="document">
                                                            <div class="modal-content">
                                                                <div class="modal-header">
                                                                    <h5 class="modal-title"
                                                                        id="deleteModalLabel${allocation.id}">Confirm
                                                                        Delete</h5>
                                                                    <button type="button" class="close"
                                                                            data-dismiss="modal" aria-label="Close">
                                                                        <span aria-hidden="true">&times;</span>
                                                                    </button>
                                                                </div>
                                                                <div class="modal-body">
                                                                    Are you sure you want to delete this allocation?
                                                                    <br>
                                                                    <strong>${allocation.name}</strong>
                                                                    <p class="text-danger mt-2">
                                                                        <i class="fas fa-exclamation-triangle mr-1"></i>
                                                                        This action cannot be undone and may affect
                                                                        existing ledger entries.
                                                                    </p>
                                                                </div>
                                                                <div class="modal-footer">
                                                                    <button type="button" class="btn btn-secondary"
                                                                            data-dismiss="modal">Cancel
                                                                    </button>
                                                                    <a href="<c:url value='/allocations/delete/${allocation.id}'/>"
                                                                       class="btn btn-danger">Delete</a>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        <c:if test="${empty assetAllocations}">
                                            <tr>
                                                <td colspan="3" class="text-center">No asset allocations found</td>
                                            </tr>
                                        </c:if>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Liability Allocations -->
                    <div class="col-lg-6">
                        <div class="card shadow mb-4">
                            <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                                <h6 class="m-0 font-weight-bold text-danger">
                                    <i class="fas fa-arrow-down mr-1"></i> Liability Allocations
                                </h6>
                            </div>
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table class="table table-bordered table-striped" width="100%" cellspacing="0">
                                        <thead>
                                        <tr>
                                            <th>Name</th>
                                            <th>Description</th>
                                            <th>Actions</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach items="${liabilityAllocations}" var="allocation">
                                            <tr>
                                                <td>
                                                    <c:if test="${not empty allocation.iconClass}">
                                                        <i class="${allocation.iconClass} mr-1"></i>
                                                    </c:if>
                                                        ${allocation.name}
                                                </td>
                                                <td>${allocation.description}</td>
                                                <td>
                                                    <a href="<c:url value='/allocations/edit/${allocation.id}'/>"
                                                       class="btn btn-sm btn-warning">
                                                        <i class="fas fa-edit"></i>
                                                    </a>
                                                    <a href="#" class="btn btn-sm btn-danger" data-toggle="modal"
                                                       data-target="#deleteModal${allocation.id}">
                                                        <i class="fas fa-trash"></i>
                                                    </a>

                                                    <!-- Delete Modal -->
                                                    <div class="modal fade" id="deleteModal${allocation.id}"
                                                         tabindex="-1" role="dialog"
                                                         aria-labelledby="deleteModalLabel${allocation.id}"
                                                         aria-hidden="true">
                                                        <div class="modal-dialog" role="document">
                                                            <div class="modal-content">
                                                                <div class="modal-header">
                                                                    <h5 class="modal-title"
                                                                        id="deleteModalLabel${allocation.id}">Confirm
                                                                        Delete</h5>
                                                                    <button type="button" class="close"
                                                                            data-dismiss="modal" aria-label="Close">
                                                                        <span aria-hidden="true">&times;</span>
                                                                    </button>
                                                                </div>
                                                                <div class="modal-body">
                                                                    Are you sure you want to delete this allocation?
                                                                    <br>
                                                                    <strong>${allocation.name}</strong>
                                                                    <p class="text-danger mt-2">
                                                                        <i class="fas fa-exclamation-triangle mr-1"></i>
                                                                        This action cannot be undone and may affect
                                                                        existing ledger entries.
                                                                    </p>
                                                                </div>
                                                                <div class="modal-footer">
                                                                    <button type="button" class="btn btn-secondary"
                                                                            data-dismiss="modal">Cancel
                                                                    </button>
                                                                    <a href="<c:url value='/allocations/delete/${allocation.id}'/>"
                                                                       class="btn btn-danger">Delete</a>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        <c:if test="${empty liabilityAllocations}">
                                            <tr>
                                                <td colspan="3" class="text-center">No liability allocations found</td>
                                            </tr>
                                        </c:if>
                                        </tbody>
                                    </table>
                                </div>
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