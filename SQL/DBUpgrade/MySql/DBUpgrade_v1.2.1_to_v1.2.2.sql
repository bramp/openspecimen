create table INTERCEPTOR_ERROR_OBJ (IDENTIFIER bigint(30) NOT NULL,ERROR_TIMESTAMP datetime, OBJECT_TYPE varchar(400),ERROR_CODE varchar(200),OBJECT_ID bigint(30),RECOVERY_DONE boolean,EVENT_CODE bigint(30),NUMBER_OF_TRY int,PROCESSOR_CLASS varchar(400),PRIMARY KEY  (IDENTIFIER));