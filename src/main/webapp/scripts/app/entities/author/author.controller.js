'use strict';

angular.module('jhipsterApp')
    .controller('AuthorController', function ($scope, Author, ParseLinks) {
        $scope.authors = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            Author.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.authors = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.create = function () {
            Author.update($scope.author,
                function () {
                    $scope.loadAll();
                    $('#saveAuthorModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Author.get({id: id}, function(result) {
                $scope.author = result;
                $('#saveAuthorModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            Author.get({id: id}, function(result) {
                $scope.author = result;
                $('#deleteAuthorConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Author.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteAuthorConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.author = {name: null, mySize: null, nextVal: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
