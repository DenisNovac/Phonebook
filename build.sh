#!/bin/bash

sbt assembly
docker build -t phonebook ./
