var notesApp = angular.module('NotesApp', ['igTruncate']);

notesApp.controller('NotesController', function NotesController($scope, $http) {

    function getAllNotes() {
        $http.post('/notes', {}).then(function (response) {
            $scope.notes = response.data;
        });
    }

    getAllNotes();

    $scope.create = function () {
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

    $scope.edit = function (note) {
        $scope.note = note;
    };

    $scope.cancel = function () {
        $scope.note = undefined;
    };

    $scope.closeAlert = function () {
        $scope.alert = undefined;
    };
});