package com.cb.gulimall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cb.common.utils.HttpUtils;
import com.cb.gulimall.member.dao.MemberLevelDao;
import com.cb.gulimall.member.entity.MemberLevelEntity;
import com.cb.gulimall.member.exception.PhoneExistException;
import com.cb.gulimall.member.exception.UserNameExistException;
import com.cb.gulimall.member.vo.MemberLoginVo;
import com.cb.gulimall.member.vo.MemberRegistVo;
import com.cb.gulimall.member.vo.SocialUser;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cb.common.utils.PageUtils;
import com.cb.common.utils.Query;

import com.cb.gulimall.member.dao.MemberDao;
import com.cb.gulimall.member.entity.MemberEntity;
import com.cb.gulimall.member.service.MemberService;

import javax.annotation.Resource;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Resource
    MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(MemberRegistVo vo) {
        MemberDao memberDao = this.baseMapper;
        MemberEntity entity = new MemberEntity();

        //设置默认等级
       MemberLevelEntity levelEntity= memberLevelDao.getDefaultLevel();
        entity.setLevelId(levelEntity.getId());

        //检查用户名个手机号是否唯一。为了让controller能感知异常，异常机制
        checkPhoneUnique(vo.getPhone());
        checkUsernameUnique(vo.getUserName());


        entity.setMobile(vo.getPhone());
        entity.setUsername(vo.getUserName());

        entity.setNickname(vo.getUserName());
        //密码加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(vo.getPassword());
        entity.setPassword(encode);

        memberDao.insert(entity);
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException{
        MemberDao memberDao = this.baseMapper;
        Integer count = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if(count>0){
            throw  new PhoneExistException();
        }

    }

    @Override
    public void checkUsernameUnique(String userName) throws UserNameExistException{
        MemberDao memberDao = this.baseMapper;
        Integer count = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));
        if(count>0){
            throw new UserNameExistException();
        }

    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        String loginacct = vo.getLoginacct();
        String password = vo.getPassword();
        MemberDao memberDao = this.baseMapper;
        MemberEntity entity = memberDao.selectOne(new QueryWrapper<MemberEntity>().eq("mobile", loginacct).or().eq("password", password));
        if(entity==null){
            return null;
        }else{
            String passwordDb = entity.getPassword();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            boolean matches = encoder.matches(password, passwordDb);
            if(matches){
                return entity;
            }else{
                return null;
            }
        }

    }

    @Override
    public MemberEntity login(SocialUser socialUser) throws Exception {
        //登录和注册合并逻辑
        String uid = socialUser.getUid();
        MemberDao memberDao = this.baseMapper;
        MemberEntity entity = memberDao.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));
        if(entity!=null){
            //这个用户已经注册
            MemberEntity update = new MemberEntity();
            update.setId(entity.getId());
            update.setAccessToken(socialUser.getAccess_token());
            update.setExpiresIn(socialUser.getExpires_in());

            memberDao.updateById(update);

            entity.setAccessToken(socialUser.getAccess_token());
            entity.setExpiresIn(socialUser.getExpires_in());
            return entity;
        }else{
            //2、没有查到但那个钱社交用户对应的记录我们就需要注册一个
            MemberEntity regist = new MemberEntity();
            //3、查询到当前社交用户社交账号（明细，性别等）
//
//            try{
//                Map<String,String> query=new HashMap<>();
//                query.put("access_token",socialUser.getAccess_token());
//                query.put("uid",socialUser.getUid());
//                HttpResponse response = HttpUtils.doGet("api.weibo.com", "/2/users/show.json", "get", new HashMap<String, String>(), query);
//                if(response.getStatusLine().getStatusCode()==200){
//                    //查询成功
//                    String json = EntityUtils.toString(response.getEntity());
//                    JSONObject jsonObject = JSON.parseObject(json);
//                    String name = jsonObject.getString("name");
//                    String gender = jsonObject.getString("gender");
//
//                    regist.setNickname(name);
//                    regist.setGender("m".equals(gender)?1:0);
//                }
//
//            }catch (Exception e){
//
//            }



            try{
                Map<String,String> query=new HashMap<>();
                query.put("access_token",socialUser.getAccess_token());
                query.put("uid",socialUser.getUid());
               // HttpResponse response = HttpUtils.doGet("api.weibo.com", "/2/users/show.json", "get", new HashMap<String, String>(), query);

                if(true){
                    //查询成功
                  //  String json = EntityUtils.toString(response.getEntity());
                  //  JSONObject jsonObject = JSON.parseObject(json);
                    String name = "chenbiao";
                    String gender = "m";

                    regist.setNickname(name);
                    regist.setGender("m".equals(gender)?1:0);
                }

            }catch (Exception e){

            }

            regist.setSocialUid(socialUser.getUid());
            regist.setAccessToken(socialUser.getAccess_token());
            regist.setExpiresIn(socialUser.getExpires_in());
            memberDao.insert(regist);
            return regist;
        }



    }

}