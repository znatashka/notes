var notesApp = angular.module('NotesApp', ['igTruncate']);

notesApp.controller('NotesController', function NotesController($scope, $http) {

    $http({method: "POST", url: "/notes"}).then(function (response) {
        $scope.notes = response.data;
    });

    $scope.showFullText = function (text) {
        alert(text);
    }
});