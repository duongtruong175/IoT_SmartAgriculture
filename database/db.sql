CREATE TABLE "DeviceData"
(
"id" INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
"device_id" VARCHAR(20) NOT NULL,
"temperature_air" DOUBLE NOT NULL,
"humidity_air" DOUBLE NOT NULL,
"humidity_soil" DOUBLE NOT NULL,
"time_send" DOUBLE NOT NULL,
PRIMARY KEY ("id")
);

CREATE TABLE "User"
(
"id" INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
"username" VARCHAR(20) NOT NULL,
"password" VARCHAR(20) NOT NULL,
"name" VARCHAR(50) NOT NULL,
"email" VARCHAR(50) NOT NULL,
"gender" VARCHAR(20) NOT NULL,
"date" VARCHAR(20) NOT NULL,
PRIMARY KEY ("username") 
);

CREATE TABLE "Area"
(
"id" INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
"name" VARCHAR(20) NOT NULL,
"device_type" VARCHAR(20) NOT NULL,
"device_id" VARCHAR(20) NOT NULL,
"user_id" INT NOT NULL,
PRIMARY KEY ("device_id") 
);