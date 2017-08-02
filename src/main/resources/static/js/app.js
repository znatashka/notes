var notesApp = angular.module('NotesApp', ['igTruncate']);

notesApp.controller('NotesController', function NotesController($scope, $http) {

    function getAllNotes() {
        $http.post('/notes', {}).then(function (response) {
            $scope.notes = response.data;
        });
    }

    getAllNotes();

    $scope.showFullText = function (text) {
        alert(text);
    };

    $scope.create = function () {
        $http.post('/create', {}).then(function (response) {
            $scope.note = response.data
        })
    };

    $scope.save = function () {
        $http.post('/save', $scope.note).then(function (response) {
            if (response.status === 200) {
                getAllNotes();
                $scope.note = undefined;
                alert('the data was successfully stored');
            }
        })
    };

    $scope.del = function (note) {
        $http.post('/del', note.id).then(function (response) {
            if (response.status === 200) {
                getAllNotes();
                alert('the data was successfully removed');
            }
        })
    };

    $scope.edit = function (note) {
        $scope.note = note;
    };

    $scope.cancel = function () {
        $scope.note = undefined;
    }
});