<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="1.10.2" name="District" tilewidth="8" tileheight="8" tilecount="960" columns="32">
 <image source="District.png" width="256" height="240"/>
 <tile id="14">
  <properties>
   <property name="no-collision" type="bool" value="true"/>
  </properties>
 </tile>
 <tile id="224" type="hero"/>
 <tile id="298" type="bouncer">
  <properties>
   <property name="bounces" type="bool" value="true"/>
   <property name="dx" type="float" value="20"/>
   <property name="dy" type="float" value="20"/>
   <property name="kills" type="bool" value="true"/>
  </properties>
  <animation>
   <frame tileid="298" duration="100"/>
   <frame tileid="202" duration="100"/>
  </animation>
 </tile>
 <tile id="435">
  <properties>
   <property name="no-collision" type="bool" value="true"/>
   <property name="savePoint" type="bool" value="true"/>
  </properties>
  <animation>
   <frame tileid="435" duration="100"/>
   <frame tileid="115" duration="100"/>
   <frame tileid="51" duration="100"/>
   <frame tileid="147" duration="100"/>
   <frame tileid="339" duration="100"/>
   <frame tileid="531" duration="100"/>
  </animation>
 </tile>
</tileset>
