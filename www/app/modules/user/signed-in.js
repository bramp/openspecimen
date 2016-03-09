angular.module('openspecimen')
  .controller('SignedInCtrl', function($scope, $rootScope, Setting, currentUser, Alerts, AuthorizationService) {
     function init() {
       $scope.alerts = Alerts.messages;
       $rootScope.currentUser = currentUser;
       
       getTrainingUrl();
     }
     
     function getTrainingUrl() {
       Setting.query({module: 'common'}).then(function(props) {
         angular.forEach(props, function(property) {
           if (property.name == 'training_url') {
             $scope.trainingUrl = property.value;
           }
         });
       });
     }

     $scope.userCreateUpdateOpts = {resource: 'User', operations: ['Create', 'Update']};
     $scope.cpReadOpts = {resource: 'CollectionProtocol', operations: ['Read']};
     $scope.containerReadOpts = {resource: 'StorageContainer', operations: ['Read']};
     $scope.orderReadOpts = {resource: 'Order', operations: ['Read']};
     $scope.shipmentReadOpts = {resource: 'ShippingAndTracking', operations: ['Read']};
     $scope.scheduledJobReadOpts = {resource: 'ScheduledJob', operations: ['Read']};

     init();
  })
