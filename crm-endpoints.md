Base URL:: /api/bookie/
	action: --Set game priority
	endPoint: tournaments/priority
	method: POST
	payload: List<Long> tournamentIds

	action: --- Get Bonus bets
	endPoint: bonus/bets
	method: GET
	payload: None
	urlParams: pagination and sorting

	action: -- create campaign
	endPoint: /campaign
	methos: POST
	payload: { file: jpg/jpeg,String description,
    String type,
    String cta,
    String date,
    String lang,
    String actionDay}

    action: --edit campaign
    endpoint: /campaign/id
    method: PATCH
    urlParams: {String status}
    pathVariable: { long id}

    action: --Get all Smeses
    endPoint: /smes
    method: GET
    urlParams: { String origin: {"limitel/ATS"}, StringDate from, StringDate to,pagination & size}


    action: -- Get sms agregations per prsp
    endPoint: /sms/report
    method: GET
    urlParams: { StringDate from,StringDate to}


Base URL:: /api/finance
	action: -- Activate/Deactivate payments per prsp per transaction type
	endPoint: /settings
	method: POST
	payload: {String service, String status{Activate/Deactivate}, int span, prsp {Lumitel,all other prsp integrated}}


Base URL:: /api/casino
		action: --Casino bets
		endPoint: /bets
		method: GET
		urlParams: { String provider{"plagmatic/aviatrix/jetx"}, pagination & size}

		action: -- Get casino bets per user
		endPoint: /user/id
		method: GET
		urlParams:{ pagination & size}
		pathVariable: {long id}


		action: -- Filter casino per provider and date
		endPoint: /filter
		method: GET
		pathParams: { String provider, StringDate from, StringDate to, pagination & size}

BASE URL:: /api/crm
		action: --Verify user
		endPoint: /user/id
		method: PUT
		urlParams: {long id}

		action: -- Settle bets in bulk
		endPoint: /settle/bulk
		method: POST
		payload: { file {CSV}}


		action: -- Create Ticket
		endPoint: /tickets
		method: POST
		payload: { 
          String name,
          String description,
          String phone,
          String issueType,
          String status}

        action: --Delete/View/Edit ticket
        methods: CRUD respectively
        payload: Per RESTful convetion
BASE URL:: /api/crm/dashboard
		action: -- Create Notifications to App
		endPoint: /dispatches
		method: POST
		payload: {String message,String type,String date {Datetime when to dispatch},String lang}


		action: -- Dispatch Notification to App
		endPoint: dispatch/message/{id}
		method: GET
		pathVariable: { long id}

