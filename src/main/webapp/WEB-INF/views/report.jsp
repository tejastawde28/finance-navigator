<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Financial Reports - Finance Navigator</title>
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

        /* Base form group styling */
        .form-group {
            min-height: 75px;
            position: relative;
            margin-bottom: 1.5rem;
        }

        /* Standard error message styling */
        .error-message {
            color: #dc3545;
            font-size: 0.875rem;
            position: absolute;
            bottom: 0;
        }

        /* Required field indicator */
        .required-field::after {
            content: " *";
            color: red;
            font-weight: bold;
        }

        /* Date range specific styles */
        .date-input-container {
            position: relative;
            margin-bottom: 25px;
        }

        .date-range-row {
            margin-bottom: 30px;
        }

        /* Input validation styling */
        input.is-invalid {
            border-color: #dc3545;
            padding-right: calc(1.5em + 0.75rem);
            background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' fill='none' stroke='%23dc3545' viewBox='0 0 12 12'%3e%3ccircle cx='6' cy='6' r='4.5'/%3e%3cpath stroke-linejoin='round' d='M5.8 3.6h.4L6 6.5z'/%3e%3ccircle cx='6' cy='8.2' r='.6' fill='%23dc3545' stroke='none'/%3e%3c/svg%3e");
            background-repeat: no-repeat;
            background-position: right calc(0.375em + 0.1875rem) center;
            background-size: calc(0.75em + 0.375rem) calc(0.75em + 0.375rem);
        }

        input.is-invalid:focus {
            border-color: #dc3545;
            box-shadow: 0 0 0 0.2rem rgba(220, 53, 69, 0.25);
        }

        /* Date-range-specific error message positioning */
        .date-input-container .error-message {
            position: absolute;
            bottom: -20px;
            left: 0;
            width: 100%;
        }

        /* Button alignment in date range form */
        .button-container {
            display: flex;
            align-items: flex-end;
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
                <a href="<c:url value='/report'/>" class="date-range-wrapper list-group-item list-group-item-action active">
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
                    <h1 class="h3 mb-0 text-gray-800">Financial Reports</h1>
                </div>

                <!-- Date Range Selection -->
                <div class="card shadow mb-4">
                    <div class="card-header py-3">
                        <h6 class="m-0 font-weight-bold text-primary">Select Date Range</h6>
                    </div>
                    <div class="card-body">
                        <form action="<c:url value='/report'/>" method="get">
                            <div class="row align-items-end">
                                <div class="col-md-4">
                                    <div class="date-input-container">
                                        <label for="startDate" class="required-field">Start Date</label>
                                        <input type="date" class="form-control ${not empty startDateError ? 'is-invalid' : ''}"
                                               id="startDate" name="startDate" value="${startDate}" required>
                                        <c:if test="${not empty startDateError}">
                                            <div class="error-message">${startDateError}</div>
                                        </c:if>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="date-input-container">
                                        <label for="endDate" class="required-field">End Date</label>
                                        <input type="date" class="form-control ${not empty endDateError ? 'is-invalid' : ''}"
                                               id="endDate" name="endDate" value="${endDate}" required>
                                        <c:if test="${not empty endDateError}">
                                            <div class="error-message">${endDateError}</div>
                                        </c:if>
                                    </div>
                                </div>
                                <div class="col-md-4 mb-3">
                                    <div class="d-flex">
                                        <button type="submit" class="btn btn-primary mr-2 mb-2">Generate Report</button>
                                        <c:if test="${not empty startDate and not empty endDate and empty startDateError and empty endDateError}">
                                            <a href="<c:url value='/report/pdf?startDate=${startDate}&endDate=${endDate}'/>" class="btn btn-danger">
                                                <i class="fas fa-file-pdf mr-1"></i> Download PDF
                                            </a>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Only show report content if dates are selected and valid -->
                <c:if test="${not empty startDate and not empty endDate and empty startDateError and empty endDateError}">
                    <!-- Period Summary -->
                    <div class="row">
                        <div class="col-lg-4 col-md-6 mb-4">
                            <div class="card border-left-success shadow h-100 py-2">
                                <div class="card-body">
                                    <div class="row no-gutters align-items-center">
                                        <div class="col mr-2">
                                            <div class="text-xs font-weight-bold text-success text-uppercase mb-1">Period Assets</div>
                                            <div class="h5 mb-0 font-weight-bold text-gray-800">
                                                <fmt:formatNumber value="${periodSummary.totalAssets}" type="currency" currencySymbol="$" />
                                            </div>
                                        </div>
                                        <div class="col-auto">
                                            <i class="fas fa-dollar-sign fa-2x text-gray-300"></i>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="col-lg-4 col-md-6 mb-4">
                            <div class="card border-left-danger shadow h-100 py-2">
                                <div class="card-body">
                                    <div class="row no-gutters align-items-center">
                                        <div class="col mr-2">
                                            <div class="text-xs font-weight-bold text-danger text-uppercase mb-1">Period Liabilities</div>
                                            <div class="h5 mb-0 font-weight-bold text-gray-800">
                                                <fmt:formatNumber value="${periodSummary.totalLiabilities}" type="currency" currencySymbol="$" />
                                            </div>
                                        </div>
                                        <div class="col-auto">
                                            <i class="fas fa-credit-card fa-2x text-gray-300"></i>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="col-lg-4 col-md-6 mb-4">
                            <div class="card border-left-info shadow h-100 py-2">
                                <div class="card-body">
                                    <div class="row no-gutters align-items-center">
                                        <div class="col mr-2">
                                            <div class="text-xs font-weight-bold text-info text-uppercase mb-1">Net Change</div>
                                            <div class="h5 mb-0 font-weight-bold text-gray-800">
                                                <fmt:formatNumber value="${periodSummary.netChange}" type="currency" currencySymbol="$" />
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

                    <!-- Transactions in Period -->
                    <div class="card shadow mb-4">
                        <div class="card-header py-3">
                            <h6 class="m-0 font-weight-bold text-primary">Period Transactions</h6>
                        </div>
                        <div class="card-body">
                            <div class="table-responsive">
                                <table class="table table-bordered table-striped" id="dataTable" width="100%" cellspacing="0">
                                    <thead>
                                    <tr>
                                        <th>Date</th>
                                        <th>Description</th>
                                        <th>Allocation</th>
                                        <th>Type</th>
                                        <th>Reference</th>
                                        <th>Amount</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${periodEntries}" var="entry">
                                        <tr>
                                            <td><fmt:formatDate pattern="MM/dd/yyyy" value="${entry.entryDate}" /></td>
                                            <td>${entry.description}</td>
                                            <td>${entry.allocation.name}</td>
                                            <td>
                                                <span class="badge ${entry.allocation.instrumentType == 'ASSET' ? 'badge-success' : 'badge-danger'}">
                                                        ${entry.allocation.instrumentType.displayName}
                                                </span>
                                            </td>
                                            <td>${entry.reference}</td>
                                            <td class="${entry.allocation.instrumentType == 'ASSET' ? 'text-success' : 'text-danger'}">
                                                <fmt:formatNumber value="${entry.allocation.instrumentType == 'ASSET' ? entry.amount : -entry.amount}"
                                                                  type="currency" currencySymbol="$" />
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty periodEntries}">
                                        <tr>
                                            <td colspan="6" class="text-center">No transactions found in this period</td>
                                        </tr>
                                    </c:if>
                                    </tbody>
                                    <tfoot>
                                    <c:if test="${not empty periodEntries}">
                                        <tr class="table-active">
                                            <td colspan="5" class="text-right font-weight-bold">TOTAL</td>
                                            <td class="${netTotalColorClass} font-weight-bold">
                                                <fmt:formatNumber value="${netTotal}" type="currency" currencySymbol="$" />
                                            </td>
                                        </tr>
                                    </c:if>
                                    </tfoot>
                                </table>
                            </div>
                        </div>
                    </div>
                </c:if>

                <c:if test="${empty startDate or empty endDate or not empty startDateError or not empty endDateError}">
                    <div class="alert alert-info text-center">
                        <i class="fas fa-info-circle mr-2"></i> Please select a valid date range to generate a financial report.
                    </div>
                </c:if>
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