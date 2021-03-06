/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.jackrabbit.oak.spi.query.fulltext;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A fulltext "or" condition.
 */
public class FullTextOr extends FullTextExpression {
    
    public final List<FullTextExpression> list;
    
    public FullTextOr(List<FullTextExpression> list) {
        this.list = list;
    }

    @Override
    public boolean evaluate(String value) {
        for (FullTextExpression e : list) {
            if (e.evaluate(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public FullTextExpression simplify() {
        Set<FullTextExpression> set = getUniqueSet(list);
        if (set.size() == 1) {
            return set.iterator().next();
        }
        ArrayList<FullTextExpression> l = new ArrayList<FullTextExpression>(
                set.size());
        l.addAll(set);
        return new FullTextOr(l);
    }
    
    static Set<FullTextExpression> getUniqueSet(
            List<FullTextExpression> list) {
        // remove duplicates, but keep order
        LinkedHashSet<FullTextExpression> set = new LinkedHashSet<FullTextExpression>(list.size());
        for (int i = 0; i < list.size(); i++) {
            set.add(list.get(i).simplify());
        }
        return set;
    }

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        int i = 0;
        for (FullTextExpression e : list) {
            if (i++ > 0) {
                buff.append(" OR ");
            }
            if (e.getPrecedence() < getPrecedence()) {
                buff.append('(');
            }
            buff.append(e.toString());
            if (e.getPrecedence() < getPrecedence()) {
                buff.append(')');
            }
        }
        return buff.toString();
    }
    
    @Override
    public int getPrecedence() {
        return PRECEDENCE_OR;
    }
    
    @Override
    public boolean accept(FullTextVisitor v) {
        return v.visit(this);
    }

}