## JioFi Dashboard

Show details about your JioFi

#### Completed

* Battery
    * Percentage - done
    * Time remaining(basic) - done
* Range - done
* LTE band (Band, bandwidth) - done
* WiFi users - done

* Download/ Upload speeds - done
* Total data used(D/U) - done

* Restart - done
* Block device - done
* Change WiFi SSID and password - done
* WPS button - done

#### Remaining

* Power save timeout - nop
* Notification(Battery reminder) - nope
* Time remaining(Advanced) - nope
* Battery Graph
* Storage link - nop
* Restore default - nop
* Change connection url - nop

#### Test (need to complete)

* Battery
    * Percentage 
    * Time remaining(basic) 
* Range 
* LTE band (Band, bandwidth) 
* WiFi users 

* Download/ Upload speeds 
* Total data used(D/U)

* Restart
* Block device
* Change WiFi SSID and password
* WPS button

#### Data shown

- Battery 
    - percentage
    - status
    - URL: http://jiofi.local.html/Device_info_ajax.cgi
```
{ "batterylevel" :'91 %', "batterystatus":'Discharging', "curr_time":'Sun 12 Aug 2018 12:35:33'} 
```

- Data speedg
    - upload (txRate)
    - download (rxRate)
    - max upload
    - max download
    - URL: http://jiofi.local.html/lte_ajax.cgi
```
{ "status":'Attached', "con_status":'1534055241', "time":'2326', "time_str":'00:00:38:46', "opmode":'FDD', "opband":'5', "rsrp":'-101 dBm', "rsrq":'-11 dB', "sinr":'4 dBm', "bandwidth":'5 MHz', "earfcn":'2540', "plmn":'405862', "apn":'jionet', "gcellID":'0022EE30', "pcellID":'195', "ecgi":'405862022EE30', "eutran":'4058620036022EE30'}

```

- Network
    - operating band
    - bandwidth
    - p cell id
    - rsrp
- Total Data
    - upload (duration_ul)
    - download (duration_dl)
- User
    - User count (act_cnt)
    - User list (userlistinfo)
    