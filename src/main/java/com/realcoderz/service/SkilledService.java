/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.service;

import java.util.Map;

/**
 *
 * @author Admin
 */
public interface SkilledService {
    
      public Map save(Map map);
      public Map getAllSkilled(Map map);
      public Map findById(Map map);
      public Map deleteById(Map map);
}
