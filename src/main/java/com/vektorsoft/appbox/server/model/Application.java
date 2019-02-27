/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vektorsoft.appbox.server.model;

import lombok.Data;

/**
 *
 * @author Vladimir Djurovic <vdjurovic@vektorsoft.com>
 */
@Data
public class Application {

    private String id;
    private String name;
    private String version;
    private String description;
    private String iconUrl;
    
    
}
