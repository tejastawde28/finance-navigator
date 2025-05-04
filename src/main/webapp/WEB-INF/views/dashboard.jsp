<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Finance Navigator - Dashboard</title>
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

        .card-dashboard {
            border-left: 4px solid;
        }

        .card-asset {
            border-left-color: #28a745;
        }

        .card-liability {
            border-left-color: #dc3545;
        }

        .card-networth {
            border-left-color: #17a2b8;
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
                <a href="<c:url value='/dashboard'/>" class="list-group-item list-group-item-action active">
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
                    <h1 class="h3 mb-0 text-gray-800">Portfolio Dashboard</h1>
                    <a href="<c:url value='/ledger/add'/>" class="d-none d-sm-inline-block btn btn-sm btn-primary shadow-sm">
                        <i class="fas fa-plus fa-sm text-white-50 mr-1"></i> Add New Entry
                    </a>
                </div>

                <!-- Financial Overview Cards -->
                <div class="row">
                    <!-- Total Assets Card -->
                    <div class="col-xl-4 col-md-6 mb-4">
                        <div class="card card-dashboard card-asset shadow h-100 py-2">
                            <div class="card-body">
                                <div class="row no-gutters align-items-center">
                                    <div class="col mr-2">
                                        <div class="text-xs font-weight-bold text-success text-uppercase mb-1">Total Assets</div>
                                        <div class="h5 mb-0 font-weight-bold text-gray-800">
                                            <fmt:formatNumber value="${summary.totalAssets}" type="currency" currencySymbol="$" />
                                        </div>
                                    </div>
                                    <div class="col-auto">
                                        <i class="fas fa-dollar-sign fa-2x text-gray-300"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Total Liabilities Card -->
                    <div class="col-xl-4 col-md-6 mb-4">
                        <div class="card card-dashboard card-liability shadow h-100 py-2">
                            <div class="card-body">
                                <div class="row no-gutters align-items-center">
                                    <div class="col mr-2">
                                        <div class="text-xs font-weight-bold text-danger text-uppercase mb-1">Total Liabilities</div>
                                        <div class="h5 mb-0 font-weight-bold text-gray-800">
                                            <fmt:formatNumber value="${summary.totalLiabilities}" type="currency" currencySymbol="$" />
                                        </div>
                                    </div>
                                    <div class="col-auto">
                                        <i class="fas fa-credit-card fa-2x text-gray-300"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Net Worth Card -->
                    <div class="col-xl-4 col-md-6 mb-4">
                        <div class="card card-dashboard card-networth shadow h-100 py-2">
                            <div class="card-body">
                                <div class="row no-gutters align-items-center">
                                    <div class="col mr-2">
                                        <div class="text-xs font-weight-bold text-info text-uppercase mb-1">Net Worth</div>
                                        <div class="h5 mb-0 font-weight-bold text-gray-800">
                                            <fmt:formatNumber value="${summary.netWorth}" type="currency" currencySymbol="$" />
                                        </div>
                                    </div>
                                    <div class="col-auto">
                                        <i class="fas fa-balance-scale fa-2x text-gray-300"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <!-- Recent Transactions Card -->
                    <div class="col-lg-8 mb-4">
                        <div class="card shadow mb-4">
                            <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                                <h6 class="m-0 font-weight-bold text-primary">Recent Transactions</h6>
                                <a href="<c:url value='/ledger'/>" class="btn btn-sm btn-info">
                                    View All
                                </a>
                            </div>
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table class="table table-striped">
                                        <thead>
                                        <tr>
                                            <th>Date</th>
                                            <th>Description</th>
                                            <th>Allocation</th>
                                            <th>Amount</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach items="${recentEntries}" var="entry">
                                            <tr>
                                                <td><fmt:formatDate pattern="MM/dd/yyyy" value="${entry.entryDate}" /></td>
                                                <td>${entry.description}</td>
                                                <td>${entry.allocation.name}</td>
                                                <td class="${entry.allocation.instrumentType == 'ASSET' ? 'text-success' : 'text-danger'}">
                                                    <fmt:formatNumber value="${entry.allocation.instrumentType == 'ASSET' ? entry.amount : -entry.amount}"
                                                                      type="currency" currencySymbol="$" />
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        <c:if test="${empty recentEntries}">
                                            <tr>
                                                <td colspan="4" class="text-center">No transactions found</td>
                                            </tr>
                                        </c:if>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Allocation Breakdown Card -->
                    <div class="col-lg-4 mb-4">
                        <div class="card shadow mb-4">
                            <div class="card-header py-3">
                                <h6 class="m-0 font-weight-bold text-primary">Allocation Breakdown</h6>
                            </div>
                            <div class="card-body">
                                <c:if test="${not empty summary.allocationBreakdown}">
                                    <c:forEach items="${summary.allocationBreakdown}" var="allocation">
                                        <h4 class="small font-weight-bold">${allocation.key}
                                            <span class="float-right">
                                                    <fmt:formatNumber value="${allocation.value}" type="currency" currencySymbol="$" />
                                                </span>
                                        </h4>
                                        <div class="progress mb-4">
                                            <div class="progress-bar" role="progressbar" style="width: ${Math.abs(allocation.value.doubleValue()) / Math.max(summary.totalAssets.doubleValue(), summary.totalLiabilities.doubleValue()) * 100}%"
                                                 aria-valuenow="${Math.abs(allocation.value.doubleValue())}" aria-valuemin="0"
                                                 aria-valuemax="${Math.max(summary.totalAssets.doubleValue(), summary.totalLiabilities.doubleValue())}"></div>
                                        </div>
                                    </c:forEach>
                                </c:if>
                                <c:if test="${empty summary.allocationBreakdown}">
                                    <div class="text-center">
                                        <p>No allocation data available</p>
                                        <a href="<c:url value='/ledger/add'/>" class="btn btn-sm btn-primary">
                                            Add Your First Entry
                                        </a>
                                    </div>
                                </c:if>
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