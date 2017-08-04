var notesApp = angular.module('NotesApp', ['igTruncate']);

notesApp.controller('NotesController', function NotesController($scope, $http) {

    function getAllNotes() {
        $http.post('/notes', {}).then(function (response) {
            $scope.notes = response.data;
        });
    }

    getAllNotes();

    $scope.create = function () {
        $scope.closeAlert();
        $http.post('/create', {}).then(function (response) {
            $scope.note = response.data
        })
    };

    $scope.save = function () {
        if ($scope.note.text) {
            $http.post('/save', $scope.note).then(function (response) {
                if (response.status === 200) {
                    getAllNotes();
                    $scope.note = undefined;
                    $scope.alert = {
                        msg: 'data was successfully stored',
                        type: 'success'
                    };
                }
            })
        } else {
            $scope.alert = {
                msg: 'text cannot be empty',
                type: 'danger'
            };
        }
    };

    $scope.del = function (note) {
        $http.post('/del', note.id).then(function (response) {
            if (response.status === 200) {
                getAllNotes();
                $scope.alert = {
                    msg: 'data was successfully removed',
                    type: 'success'
                };
            }
        })
    };

    $scope.showHistory = function (note) {
        $scope.closeAlert();
        $scope.closeEdit();

        $http.post('/history', note.id).then(function (response) {
            $scope.history = response.data;
        })
    };

    $scope.edit = function (note) {
        $scope.closeAlert();
        $scope.closeHistory();

        $scope.note = note;
    };

    $scope.closeEdit = function () {
        $scope.note = undefined;
    };

    $scope.closeAlert = function () {
        $scope.alert = undefined;
    };

    $scope.closeHistory = function () {
        $scope.history = undefined;
    };
});