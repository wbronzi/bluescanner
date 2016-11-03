DB Setup 

-- --------------------------------------------------------

--
-- Table structure for table `bc`
--

CREATE TABLE IF NOT EXISTS `bc` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `session_id` varchar(36) NOT NULL,
  `location_id` varchar(36) NOT NULL,
  `timestamp` timestamp NULL DEFAULT NULL,
  `mac` varchar(17) NOT NULL,
  `type` int(11) NOT NULL,
  `rssi` int(11) NOT NULL,
  `device_name` text NOT NULL,
  `bc_class` text NOT NULL,
  `bc_class_resolved` text,
  PRIMARY KEY (`id`),
  KEY `FOREIGN` (`session_id`,`location_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=100140 ;

-- --------------------------------------------------------

--
-- Table structure for table `ble`
--

CREATE TABLE IF NOT EXISTS `ble` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `session_id` varchar(36) NOT NULL,
  `location_id` varchar(36) NOT NULL,
  `timestamp` timestamp NULL DEFAULT NULL,
  `mac` varchar(17) NOT NULL,
  `rssi` int(11) NOT NULL,
  `device_name` text NOT NULL,
  `ble_adv_data` text NOT NULL,
  `ble_adv_data_resolved` text,
  PRIMARY KEY (`id`),
  KEY `FOREIGN` (`session_id`,`location_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=285584 ;

-- --------------------------------------------------------

--
-- Table structure for table `locations`
--

CREATE TABLE IF NOT EXISTS `locations` (
  `location_id` varchar(36) NOT NULL,
  `session_id` varchar(36) NOT NULL,
  `timestamp` timestamp NULL DEFAULT NULL,
  `latitude` float NOT NULL,
  `longitude` float NOT NULL,
  `speed` float NOT NULL,
  `bearing` float NOT NULL,
  `altitude` float NOT NULL,
  `accuracy` float NOT NULL,
  `clazz` int(11) DEFAULT NULL,
  `clazz_resolved` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`location_id`),
  KEY `FOREIGN` (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `trips`
--

CREATE TABLE IF NOT EXISTS `trips` (
  `session_id` varchar(36) NOT NULL,
  `imei` text NOT NULL,
  `transport` text,
  `user_id` text,
  `timestamp_start` timestamp NULL DEFAULT NULL,
  `timestamp_end` timestamp NULL DEFAULT NULL,
  `app_version` text,
  PRIMARY KEY (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` text NOT NULL,
  `password` char(60) NOT NULL,
  `imei` text,
  `role` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;
