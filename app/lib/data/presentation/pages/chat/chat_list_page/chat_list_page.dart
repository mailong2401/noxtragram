import 'package:flutter/material.dart';

import 'activity_list.dart';
import 'chat_list.dart';

// class SimpleComboBox extends StatefulWidget {
//   final List<String> items;
//   final String? initialValue;
//   final Function(String)? onItemSelected;
//
//   const SimpleComboBox({
//     Key? key,
//     required this.items,
//     this.initialValue,
//     this.onItemSelected,
//   }) : super(key: key);
//
//   @override
//   State<SimpleComboBox> createState() => _SimpleComboBoxState();
// }
//
// class _SimpleComboBoxState extends State<SimpleComboBox> {
//   late String _selectedValue;
//
//   @override
//   void initState() {
//     super.initState();
//     _selectedValue = widget.initialValue ?? widget.items.first;
//   }
//
//   @override
//   Widget build(BuildContext context) {
//     return Container(
//       decoration: BoxDecoration(
//         //border: Border.all(color: Colors.red),
//         borderRadius: BorderRadius.circular(8),
//       ),
//       child: DropdownButton<String>(
//         value: _selectedValue,
//         icon: Icon(Icons.arrow_drop_down, color: Colors.white, size: 30,),
//         underline: SizedBox(), // Ẩn gạch chân
//         isExpanded: false,
//         style: const TextStyle(color: Colors.white, fontSize: 17),
//         dropdownColor: Colors.black,
//         items: widget.items.map<DropdownMenuItem<String>>((String value) {
//           return DropdownMenuItem<String>(
//             value: value,
//             child: Padding(
//               padding: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
//               child: Text(value, style: const TextStyle(color: Colors.white,fontSize: 17)),
//             ),
//           );
//         }).toList(),
//         onChanged: (String? newValue) {
//           if (newValue != null) {
//             setState(() {
//               _selectedValue = newValue;
//             });
//             // Gọi callback khi chọn item
//             widget.onItemSelected?.call(newValue);
//           }
//         },
//       ),
//     );
//   }
// }

class ChatListPage extends StatelessWidget {
   ChatListPage({super.key});

  // Danh sách tài khoản mẫu
  final List<String> accounts = [
    'minhtris2005',
    'trisdeptrai',
    'tris_pro',
    'tris_super'
  ];

  // Xử lý khi chọn tài khoản
  void _handleAccountSelected(String accountName) {
    print('✅ Đã chọn tài khoản: $accountName');
    // Xử lý logic sau này ở đây
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.black,
        iconTheme: IconThemeData(color: Colors.white),
        leading: IconButton(
          icon: Icon(Icons.arrow_back_ios),
          onPressed: () => Navigator.pop(context),
        ),
        title: Row(
          children: [
            // Sử dụng SimpleComboBox
            Expanded(
              child: Container(
                //width: 0,
                child: SimpleComboBox(
                  items: accounts,
                  initialValue: accounts.first,
                  onItemSelected: _handleAccountSelected,
                ),
              ),
            ),
            IconButton(
              icon: Icon(Icons.import_contacts),
              onPressed: () {

              },
            ),
          ],
        ),
      ),

      body: Container(
        color: Colors.black,
        child: Column(
          children: [
            Container(
              margin: EdgeInsets.symmetric(vertical: 10, horizontal: 10),
              width: double.infinity,
              height: 50,
              child: OutlinedButton(
                onPressed: (){},
                style: OutlinedButton.styleFrom(
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(40.0),
                  ),
                  foregroundColor: Colors.grey,
                  backgroundColor: Colors.transparent,
                ),
                child: Row(
                  children: [
                    Icon(Icons.search),
                    const Text(
                      ' Hỏi Meta AI hoặc tìm kiếm',
                      style: TextStyle(fontSize: 18),
                    ),
                  ],
                ),
              ),
            ),
            // DANH SÁCH NGƯỜI DÙNG SCROLL NGANG
            HorizontalUserList(
              users: UserData.activeUsers,
              //onUserTap: _handleUserTap,
            ),

            // TITLE DANH SÁCH CHAT
            Padding(
              padding: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
              child: Row(
                children: [
                  OutlinedButton(
                    onPressed: (){},
                    style: OutlinedButton.styleFrom(
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(40.0),
                      ),
                      foregroundColor: Colors.white,
                      backgroundColor: Colors.transparent,
                    ),
                    child: const Text(
                      ' Chính',
                      style: TextStyle(fontSize: 15),
                    ),
                  ),
                  Spacer(),
                  OutlinedButton(
                    onPressed: (){},
                    style: OutlinedButton.styleFrom(
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(40.0),
                      ),
                      foregroundColor: Colors.white,
                      backgroundColor: Colors.transparent,
                    ),
                    child: const Text(
                      ' Tin  nhắn đang chờ',
                      style: TextStyle(fontSize: 15),
                    ),
                  ),
                  Spacer(),
                  OutlinedButton(
                    onPressed: (){},
                    style: OutlinedButton.styleFrom(
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(40.0),
                      ),
                      foregroundColor: Colors.white,
                      backgroundColor: Colors.transparent,
                    ),
                    child: const Text(
                      ' Chung',
                      style: TextStyle(fontSize: 15),
                    ),
                  ),
                ],
              ),
            ),
            Expanded(
              child: ListView.builder(
                padding: EdgeInsets.all(16),
                itemCount: 20,
                itemBuilder: (context, index) => Card(
                  margin: EdgeInsets.symmetric(vertical: 8),
                  child: ListTile(
                    leading: _buildLeadingIcon(index), // Thêm icon bên trái
                    title: Text('Chat ${index + 1}'),
                    subtitle: Text('Tin nhắn cuối...'),
                    trailing: Text('12:${index + 10}'), // Thêm thời gian
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
   // Hàm xây dựng icon bên trái tùy theo index
   Widget _buildLeadingIcon(int index) {
     // Có thể thay đổi icon theo loại chat
     if (index % 4 == 0) {
       return CircleAvatar(
         backgroundColor: Colors.blue,
         child: Icon(Icons.person, color: Colors.white),
       );
     } else if (index % 4 == 1) {
       return CircleAvatar(
         backgroundColor: Colors.green,
         child: Icon(Icons.group, color: Colors.white),
       );
     } else if (index % 4 == 2) {
       return CircleAvatar(
         backgroundColor: Colors.orange,
         child: Icon(Icons.business, color: Colors.white),
       );
     } else {
       return CircleAvatar(
         backgroundColor: Colors.purple,
         child: Icon(Icons.chat, color: Colors.white),
       );
     }
   }
   // Widget _buildLeadingIcon(int index) {
   //   // Nếu có URL ảnh thì dùng, không thì dùng avatar mặc định
   //   return CircleAvatar(
   //     backgroundImage: NetworkImage('https://picsum.photos/100?random=$index'),
   //     backgroundColor: Colors.grey[300],
   //     child: Icon(Icons.person, color: Colors.white), // Fallback icon
   //   );
   // }
}