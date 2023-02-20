<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <!-- Required Meta Tags Always Come First -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Title -->
    <title>Products</title>

    <!-- Favicon -->
    <link rel="shortcut icon" href="favicon.ico">

    <!-- Font -->
    <link href="https://fonts.googleapis.com/css2?family=Open+Sans:wght@400;600&display=swap" rel="stylesheet">

    <!-- CSS Implementing Plugins -->
    <link rel="stylesheet" href="/webapp/assets/css/vendor.min.css">
    <!-- href="/assets/css/vendor.min.css"> -->
    <!-- "assets\css\vendor.min.css -->
    <link rel="stylesheet" href="/webapp/assets/vendor/icon-set/style.css">
    <!-- "assets\vendor\icon-set\style.css" -->
    <link href="https://cdn.jsdelivr.net/npm/@sweetalert2/theme-dark@4/dark.css" rel="stylesheet">



    <!-- CSS Front Template -->
    <link rel="stylesheet" href="../assets/css/theme.min.css?v1.0">
    <!-- assets\css\theme.min.css?v=1.0 -->
</head>

<body class="footer-offset">

<!-- giữ lại -->
<main id="content" role="main" class="main">
    <!-- Content -->
    <div class="content container-fluid">
        <!-- Page Header -->
        <div class="page-header">
            <div class="row align-items-center mb-3">
                <div class="col-sm mb-2 mb-sm-0">
                    <label><h1><a href="/product">Product</a></h1></label>
                </div>
                <div class="col-sm-auto">
                    <a class="btn btn-primary" href="/product?action=create">Add product</a>
                </div>
            </div>
            <!-- End Row -->
        </div>
        <!-- End Page Header -->

        <div class="row justify-content-end mb-3">
            <div class="col-lg">
                <!-- Datatable Info -->
                <div id="datatableCounterInfo" style="display: none;">
                    <div class="d-sm-flex justify-content-lg-end align-items-sm-center">
                <span class="d-block d-sm-inline-block font-size-sm mr-3 mb-2 mb-sm-0">
                  <span id="datatableCounter">0</span>
                  Selected
                </span>
                        <a class="btn btn-sm btn-outline-danger mb-2 mb-sm-0 mr-2" href="javascript:;">
                            <i class="tio-delete-outlined"></i> Delete
                        </a>
                        <a class="btn btn-sm btn-white mb-2 mb-sm-0 mr-2" href="javascript:;">
                            <i class="tio-archive"></i> Archive
                        </a>
                        <a class="btn btn-sm btn-white mb-2 mb-sm-0 mr-2" href="javascript:;">
                            <i class="tio-publish"></i> Publish
                        </a>
                        <a class="btn btn-sm btn-white mb-2 mb-sm-0" href="javascript:;">
                            <i class="tio-clear"></i> Unpublish
                        </a>
                    </div>
                </div>
                <!-- End Datatable Info -->
            </div>
        </div>
        <!-- End Row -->

        <!-- Card -->
        <div class="row">
            <!-- Header -->
                <div class="col-12" style="display: flex">
                    <div class="col-6">
                        <form method="get" action="/product">
                            <!-- Search -->
                            <div class="input-group input-group-merge input-group-flush">
                                <input  type="search" class="form-control" name="search" placeholder="Name product" value="${requestScope.search}">
                                <select name="idCategorie" class="col-5 form-control">
                                    <option value="-1">All</option>
                                        <c:forEach items="${requestScope.categories}" var="categorie">
                                            <option value="${categorie.getId()}"
                                                    <c:if test="${categorie.getId() == requestScope.product.getIdCategory()}"></c:if>>
                                                    ${categorie.getName()}
                                            </option>
                                        </c:forEach>
                                </select>
                                <button class="btn-soft-dark" type="submit" >Search</button>


                            </div>
                            <!-- End Search -->
                        </form>
                        <%--       phân trang         --%>

                    </div>


                </div>
                <!-- End Row -->

            <!-- End Header -->

            <!-- Table -->
                <div class="table-responsive datatable-custom">

                <!-- Nhận thông báo nếu message phía sever gửi về là xóa hoặc edit thì báo alert  -->
                <div class="row">
                    <c:if test="${requestScope.message == 'delete'}">
                        <script>
                            window.onload = ()=>{
                                Swal.fire(
                                    'Deleted!',
                                    'Your file has been deleted.',
                                    'success'
                                )
                            }
                        </script>
                    </c:if>

                    <c:if test="${requestScope.message == 'edit'}">
                        <script>
                            window.onload = ()=>{
                                Swal.fire(
                                    'Edit!',
                                    'Thông tin không hợp lệ.',
                                    'success'
                                )
                            }
                        </script>
                    </c:if>


                    <table id="datatable" class="table table-borderless table-thead-bordered table-nowrap table-align-middle card-table" data-hs-datatables-options='{
                     "columnDefs": [{
                        "targets": [0, 4, 9],
                        "width": "5%",
                        "orderable": false
                      }],
                     "order": [],
                     "info": {
                       "totalQty": "#datatableWithPaginationInfoTotalQty"
                     },
                     "search": "#datatableSearch",
                     "entries": "#datatableEntries",
                     "pageLength": 12,
                     "isResponsive": false,
                     "isShowPaging": false,
                     "pagination": "datatablePagination"
                   }'>
                        <thead class="thead-light">
                        <tr>
                            <th></th>
                            <th class="table-column-pl-0">Product</th>
                            <th>Price</th>
                            <th>Description</th>
                            <th>Category</th>
                            <th>Action</th>
                        </tr>
                        </thead>

                        <tbody>
                        <!-- Thả vòng lặp for -->
                        <c:forEach var="product" items="${requestScope.products}">
                            <tr>
                                <td class="table-column-pr-0">
                                </td>
                                <td class="table-column-pl-0">
                                    <a class="media align-items-center">
                                        <!-- thả hình product -->
                                        <img class="avatar avatar-lg mr-3" src="/images/${product.getImage()}">
                                        <div class="media-body">
                                            <!-- Thả name product -->
                                            <h5 class="text-hover-primary mb-0">${product.getName()}</h5>

                                        </div>
                                    </a>
                                </td>
                                <td>${product.getPrice()}</td>
                                <td>${product.getDescription()}</td>
                                <td>
                                    <c:forEach var="category" items="${requestScope.categories}">
                                        <c:if test="${category.getId() == product.getIdCategory()}">
                                            ${category.getName()}
                                        </c:if>
                                    </c:forEach>
                                </td>
                                <td>
                                    <div class="btn-group" role="group">
                                        <!-- Nút edit -->
                                        <a class="btn btn-sm btn-white" href="/product?action=edit&id=${product.getId()}">
                                            <i class="tio-edit"></i> Edit
                                        </a>
                                        <!-- Nút xóa -->
                                        <a class="btn btn-sm btn-white" onclick="handleDeleteClick(${product.getId()})">
                                            <i class="tio-remove"></i>Remove
                                        </a>

                                    </div>
                                </td>
                            </tr>
                        </c:forEach>

                        </tbody>

                    </table>
                    <%--       view phân trang             --%>



                    <!-- Form ẩn gửi idproduct lên doPost -->
                    <form id="frmDelete" method="post" action="/product?action=delete">
                        <input type="hidden" name="idDelete" value="" id="idDelete">
                    </form>

                </div>
                    <div class="justify-content-sm-center " style="height: 50px">
                        <ul class="pagination pagination-split float-right mb-0">
                            <c:if test="${currentPage != 1}">
                                <li class="page-item">
                                    <a href="/product?search=${requestScope.search}&idCategorie=${requestScope.idCategorie}&page=${currentPage-1}" class="page-link">Back</a>
                                </li>
                            </c:if>
                            <c:forEach begin="1" end="${noOfPages}" var="i">
                                <c:choose>
                                    <c:when test="${currentPage eq i}">
                                        <li class="page-item active">
                                            <a href="/product?search=${requestScope.kw}&idCategorie=${requestScope.ct}&page=${i}" class="page-link">${i}</a>
                                        </li>
                                    </c:when>
                                    <c:otherwise>
                                        <li class="page-item">
                                            <a href="/product?search=${requestScope.kw}&idCategorie=${requestScope.ct}&page=${i}" class="page-link">${i}</a>
                                        </li>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                            <c:if test="${currentPage lt noOfPages}">
                                <li class="page-item">
                                    <a href="/product?search=${requestScope.kw}&idCategorie=${requestScope.ct}&page=${currentPage+1}" class="page-link">Next</a>
                                </li>
                            </c:if>
                        </ul>
                    </div>



                <!-- End Table -->


            </div>
            <!-- End Card -->
        </div>
        <!-- End Content -->
    </div>
</main>


</main>
</body>

<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.js"></script>
<script>
    function handleDeleteClick(idProduct) {
        document.getElementById("idDelete").value = idProduct;
        Swal.fire({
            title: 'Are you sure?',
            // text: "You won't be able to revert this!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Yes, delete it!'
        }).then((result) => {
            if (result.isConfirmed) {
                document.getElementById("frmDelete").submit();
                // Swal.fire(
                //     'Deleted!',
                //     'Your file has been deleted.',
                //     'success'
                // )
            }
        })
        return false;
    }
</script>

</html>
