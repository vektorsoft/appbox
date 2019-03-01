/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */


package com.vektorsoft.appbox.server.apps;

import com.vektorsoft.appbox.server.util.IdGenerator;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * @author Vladimir Djurovic <vdjurovic@vektorsoft.com>
 */
@Data
@Entity(name = "application")
public class Application {

    @Id
    @GenericGenerator(name = "id_generator", strategy = "com.vektorsoft.appbox.server.util.IdGenerator")
    @GeneratedValue(generator = "id_generator")
    @Column(name = "app_id")
    private String id;

    @Column(name = "app_name")
    private String name;
    private String headline;
    private String description;

    @Column(name = "app_img_url")
    private String appImageUrl;
    
}
